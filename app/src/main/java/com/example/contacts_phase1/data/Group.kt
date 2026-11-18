package com.example.contacts_phase1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A contact group such as Family, Work or University.
 * Groups live in their own table; each contact points to one with [Contact.groupId].
 */
@Entity(tableName = "contact_groups")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)
