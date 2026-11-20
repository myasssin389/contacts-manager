package com.example.contacts_phase1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Every database operation the app needs (Create, Read, Update, Delete).
 * Room writes the code for these methods at build time.
 * Queries return a Flow, so the screens update by themselves when the data changes.
 */
@Dao
interface ContactDao {

    // Create: returns the new row's id
    @Insert
    suspend fun insert(contact: Contact): Long

    // Read
    @Query("SELECT * FROM contacts ORDER BY name COLLATE NOCASE")
    fun getAll(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getById(id: Long): Flow<Contact?>

    // A one-time read (no Flow), for when the repository needs the saved row once.
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun findById(id: Long): Contact?

    @Query(
        "SELECT * FROM contacts " +
            "WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' " +
            "ORDER BY name COLLATE NOCASE"
    )
    fun search(query: String): Flow<List<Contact>>

    // In the order they were added, so "Other" comes last.
    @Query("SELECT * FROM contact_groups ORDER BY id")
    fun getGroups(): Flow<List<Group>>

    // Update
    @Update
    suspend fun update(contact: Contact)

    @Query("UPDATE contacts SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    // Delete
    @Delete
    suspend fun delete(contact: Contact)
}
