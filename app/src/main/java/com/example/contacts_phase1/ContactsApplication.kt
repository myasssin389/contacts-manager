package com.example.contacts_phase1

import android.app.Application
import com.example.contacts_phase1.data.ContactsDatabase
import com.example.contacts_phase1.data.ContactsRepository
import com.example.contacts_phase1.data.SettingsRepository

/**
 * Runs before any Activity (it is registered in AndroidManifest.xml).
 * It creates the database and the repositories once, for the whole app.
 */
class ContactsApplication : Application() {

    val contactsRepository: ContactsRepository by lazy {
        ContactsRepository(ContactsDatabase.getDatabase(this).contactDao())
    }

    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
}
