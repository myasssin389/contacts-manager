package com.example.contacts_phase1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contacts_phase1.data.Contact
import com.example.contacts_phase1.intents.SharedContact
import com.example.contacts_phase1.intents.SharedTextParser
import com.example.contacts_phase1.ui.ContactViewModel
import com.example.contacts_phase1.ui.components.PhotoPicker
import com.example.contacts_phase1.ui.edit.ContactEditScreen
import com.example.contacts_phase1.ui.theme.ContactsTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Add or edit a contact. It can be opened three ways:
 *  1. with no extra: a new, empty contact;
 *  2. with EXTRA_CONTACT_ID: edit that contact;
 *  3. by another app sharing text (ACTION_SEND, see the intent filter in AndroidManifest.xml).
 */
class EditContactActivity : ComponentActivity() {

    private val viewModel: ContactViewModel by viewModels { ContactViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactId = intent.getLongExtra(EXTRA_CONTACT_ID, NEW_CONTACT)
        val shared = sharedContactFromIntent()

        enableEdgeToEdge()
        setContent {
            ContactsTheme {
                val groups by viewModel.groups.collectAsStateWithLifecycle()
                val loaded by remember(contactId) { startingContact(contactId, shared) }
                    .collectAsStateWithLifecycle(initialValue = null)

                // The form appears once the contact is loaded, so its fields start filled in.
                loaded?.let { initial ->
                    ContactEditScreen(
                        initial = initial,
                        groups = groups,
                        onSave = { contact -> viewModel.save(contact, onSaved = ::finish) },
                        onCancel = ::finish,
                        // Person 6's photo picker fills the form's photo slot.
                        photoPicker = { photoPath, name, onPhotoChange ->
                            PhotoPicker(photoPath = photoPath, name = name, onPhotoPicked = onPhotoChange)
                        },
                    )
                }
            }
        }
    }

    private fun startingContact(contactId: Long, shared: SharedContact?): Flow<Contact?> =
        if (contactId == NEW_CONTACT) {
            flowOf(
                Contact(
                    name = shared?.name.orEmpty(),
                    phone = shared?.phone.orEmpty(),
                    email = shared?.email.orEmpty(),
                )
            )
        } else {
            viewModel.contact(contactId)
        }

    /** Receiving an implicit Intent: another app shared plain text with us. */
    private fun sharedContactFromIntent(): SharedContact? {
        if (intent.action != Intent.ACTION_SEND) return null
        if (intent.type?.startsWith("text/") != true) return null
        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null
        return SharedTextParser.parse(text)
    }

    companion object {
        const val EXTRA_CONTACT_ID = "com.example.contacts_phase1.EXTRA_CONTACT_ID"
        private const val NEW_CONTACT = 0L
    }
}
