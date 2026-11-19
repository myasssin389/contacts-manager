package com.example.contacts_phase1.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.contacts_phase1.ContactsApplication
import com.example.contacts_phase1.data.Contact
import com.example.contacts_phase1.data.ContactsRepository
import com.example.contacts_phase1.data.Group
import com.example.contacts_phase1.data.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Everything the list screen shows, in one read-only object. */
data class ContactListUiState(
    val contacts: List<Contact> = emptyList(),
    val groups: List<Group> = emptyList(),
    val query: String = "",
    val sortAscending: Boolean = true,
    val favoritesOnly: Boolean = false,
)

/**
 * Holds the screen data and survives screen rotation.
 * The UI only reads [listState] and calls the functions below; it never touches the database.
 */
class ContactViewModel(
    private val contacts: ContactsRepository,
    private val settings: SettingsRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val favoritesOnly = MutableStateFlow(false)

    // Each time the search text changes, switch to the database query for the new text.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResults: Flow<List<Contact>> = query.flatMapLatest { text -> contacts.search(text) }

    val listState: StateFlow<ContactListUiState> = combine(
        searchResults,
        contacts.groups,
        query,
        settings.sortAscending,
        favoritesOnly,
    ) { found, groups, text, ascending, onlyFavorites ->
        val shown = if (onlyFavorites) found.filter { it.isFavorite } else found
        ContactListUiState(
            // The database already sorts A to Z; reverse the list for Z to A.
            contacts = if (ascending) shown else shown.reversed(),
            groups = groups,
            query = text,
            sortAscending = ascending,
            favoritesOnly = onlyFavorites,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ContactListUiState(),
    )

    val groups: StateFlow<List<Group>> = contacts.groups.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun contact(id: Long): Flow<Contact?> = contacts.getContact(id)

    fun onQueryChange(text: String) {
        query.value = text
    }

    fun onFavoritesOnlyChange(onlyFavorites: Boolean) {
        favoritesOnly.value = onlyFavorites
    }

    fun toggleSortOrder() {
        viewModelScope.launch {
            settings.setSortAscending(!settings.sortAscending.first())
        }
    }

    fun toggleFavorite(contact: Contact) {
        viewModelScope.launch { contacts.setFavorite(contact.id, !contact.isFavorite) }
    }

    /** Saves in the background, then calls [onSaved] (for example to close the screen). */
    fun save(contact: Contact, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            contacts.save(contact)
            onSaved()
        }
    }

    fun delete(contact: Contact, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            contacts.delete(contact)
            onDeleted()
        }
    }

    companion object {
        /** Tells Android how to build this ViewModel with the repositories it needs. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[AndroidViewModelFactory.APPLICATION_KEY] as ContactsApplication
                ContactViewModel(app.contactsRepository, app.settingsRepository)
            }
        }
    }
}
