package com.example.contacts_phase1.data

import kotlinx.coroutines.flow.Flow

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

    /** Inserts a new contact (id 0) or updates an existing one, and returns its id. */
    suspend fun save(contact: Contact): Long {
        if (contact.id == 0L) return dao.insert(contact)
        dao.update(contact)
        return contact.id
    }

    suspend fun setFavorite(id: Long, favorite: Boolean) = dao.setFavorite(id, favorite)

    suspend fun delete(contact: Contact) = dao.delete(contact)
}
