package com.example.contacts_phase1.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * One row of the contacts table.
 *
 * Every field is a `val`, so a contact never changes in place: to edit one, make a copy
 * with new values, for example `contact.copy(phone = "0100 000 0000")`, and save the copy.
 */
@Entity(
    tableName = "contacts",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            // Deleting a group keeps its contacts and only clears their group
            // (Lab 4 used CASCADE, which would delete the contacts too).
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("groupId")],
)
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val city: String = "",
    val birthday: LocalDate? = null,
    /** Absolute path of the photo copied into the app's own storage, or null for no photo. */
    val photoPath: String? = null,
    val isFavorite: Boolean = false,
    val groupId: Long? = null,
)
