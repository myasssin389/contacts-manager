package com.example.contacts_phase1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contacts_phase1.ui.ContactViewModel
import com.example.contacts_phase1.ui.list.ContactListScreen
import com.example.contacts_phase1.ui.theme.ContactsTheme

/** The first screen the user sees: the contact list. */
class MainActivity : ComponentActivity() {

    // by viewModels: the same ViewModel comes back after a rotation, so nothing is reloaded.
    private val viewModel: ContactViewModel by viewModels { ContactViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContactsTheme {
                val state by viewModel.listState.collectAsStateWithLifecycle()
                ContactListScreen(
                    state = state,
                    onQueryChange = viewModel::onQueryChange,
                    onToggleSort = viewModel::toggleSortOrder,
                    onFavoritesOnlyChange = viewModel::onFavoritesOnlyChange,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onContactClick = { contact -> openDetail(contact.id) },
                    onAddClick = ::openNewContact,
                )
            }
        }
    }

    /** Explicit Intent: names the exact Activity to start and passes only the contact's id. */
    private fun openDetail(contactId: Long) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_CONTACT_ID, contactId)
        startActivity(intent)
    }

    /** Explicit Intent with no extra: the edit screen opens empty, for a new contact. */
    private fun openNewContact() {
        startActivity(Intent(this, EditContactActivity::class.java))
    }
}
