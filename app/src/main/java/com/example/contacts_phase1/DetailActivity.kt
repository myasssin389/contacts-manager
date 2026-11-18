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
import com.example.contacts_phase1.ui.ContactViewModel
import com.example.contacts_phase1.ui.detail.ContactActions
import com.example.contacts_phase1.ui.detail.ContactDetailScreen
import com.example.contacts_phase1.ui.theme.ContactsTheme

/** Shows one contact. MainActivity starts it with an explicit Intent carrying the contact's id. */
class DetailActivity : ComponentActivity() {

    private val viewModel: ContactViewModel by viewModels { ContactViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Read the extra that MainActivity put into the Intent.
        val contactId = intent.getLongExtra(EXTRA_CONTACT_ID, NO_ID)
        if (contactId == NO_ID) {
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            ContactsTheme {
                val contact by remember(contactId) { viewModel.contact(contactId) }
                    .collectAsStateWithLifecycle(initialValue = null)
                val groups by viewModel.groups.collectAsStateWithLifecycle()

                contact?.let { current ->
                    ContactDetailScreen(
                        contact = current,
                        groupName = groups.firstOrNull { group -> group.id == current.groupId }?.name,
                        onBack = ::finish,
                        onEdit = { openEditor(current.id) },
                        onDelete = { viewModel.delete(current, onDeleted = ::finish) },
                        onToggleFavorite = { viewModel.toggleFavorite(current) },
                        // Person 6's Call / SMS / Email / Map / Share buttons fill the actions slot.
                        actions = { ContactActions(contact = current) },
                    )
                }
            }
        }
    }

    /** Explicit Intent to the edit screen, passing the same id along. */
    private fun openEditor(contactId: Long) {
        val intent = Intent(this, EditContactActivity::class.java)
        intent.putExtra(EditContactActivity.EXTRA_CONTACT_ID, contactId)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_CONTACT_ID = "com.example.contacts_phase1.EXTRA_CONTACT_ID"
        private const val NO_ID = -1L
    }
}
