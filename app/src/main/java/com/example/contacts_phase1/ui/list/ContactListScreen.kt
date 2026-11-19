package com.example.contacts_phase1.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.contacts_phase1.R
import com.example.contacts_phase1.data.Contact
import com.example.contacts_phase1.ui.ContactListUiState

/**
 * The home screen: search box, favorites filter, A–Z / Z–A sort and the contact list.
 * It is stateless: it shows [state] and reports clicks through the lambdas (state hoisting).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    state: ContactListUiState,
    onQueryChange: (String) -> Unit,
    onToggleSort: () -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit,
    onToggleFavorite: (Contact) -> Unit,
    onContactClick: (Contact) -> Unit,
    onAddClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contacts_title)) },
                actions = {
                    // Shows the current order; tapping switches it.
                    TextButton(onClick = onToggleSort) {
                        Text(
                            stringResource(if (state.sortAscending) R.string.sort_a_to_z else R.string.sort_z_to_a)
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_contact))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                FilterChip(
                    selected = state.favoritesOnly,
                    onClick = { onFavoritesOnlyChange(!state.favoritesOnly) },
                    label = { Text(stringResource(R.string.favorites_only)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    },
                )
            }

            if (state.contacts.isEmpty()) {
                EmptyState(searching = state.query.isNotBlank() || state.favoritesOnly)
            } else {
                val groupNames = state.groups.associate { group -> group.id to group.name }
                // Adaptive grid: one column on a phone held upright,
                // two or more in landscape and on tablets (responsive layout).
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 320.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.contacts, key = { contact -> contact.id }) { contact ->
                        ContactRow(
                            contact = contact,
                            groupName = contact.groupId?.let { id -> groupNames[id] },
                            onClick = { onContactClick(contact) },
                            onToggleFavorite = { onToggleFavorite(contact) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(searching: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Text(
            text = stringResource(if (searching) R.string.no_matches else R.string.no_contacts),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
