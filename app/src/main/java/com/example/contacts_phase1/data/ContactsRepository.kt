package com.example.contacts_phase1.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

/**
 * The one place the rest of the app gets contacts from.
 * Screens and ViewModels never talk to Room directly, only to this class.
 */
class ContactsRepository(private val dao: ContactDao) {

    val groups: Flow<List<Group>> = dao.getGroups()

    /** All contacts when [query] is blank, otherwise the ones whose name or phone matches. */
    fun search(query: String): Flow<List<Contact>> =
        if (query.isBlank()) dao.getAll() else dao.search(query.trim())

    fun getContact(id: Long): Flow<Contact?> = dao.getById(id)

    /**
     * Inserts a new contact (id 0) or updates an existing one, and returns its id.
     * If the photo was changed, the old photo file is deleted so it does not keep using storage.
     */
    suspend fun save(contact: Contact): Long {
        if (contact.id == 0L) return dao.insert(contact)
        val oldPhoto = dao.findById(contact.id)?.photoPath
        dao.update(contact)
        if (oldPhoto != contact.photoPath) deletePhoto(oldPhoto)
        return contact.id
    }

    suspend fun setFavorite(id: Long, favorite: Boolean) = dao.setFavorite(id, favorite)

    /** Deletes the contact and its photo file. */
    suspend fun delete(contact: Contact) {
        dao.delete(contact)
        deletePhoto(contact.photoPath)
    }

    // Deleting a file is slow work, so it runs on the IO dispatcher, off the main thread.
    private suspend fun deletePhoto(path: String?) {
        if (path == null) return
        withContext(Dispatchers.IO) { File(path).delete() }
    }
}
