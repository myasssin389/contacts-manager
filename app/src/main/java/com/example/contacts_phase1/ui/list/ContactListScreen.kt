package com.example.contacts_phase1.ui.list

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.contacts_phase1.R
import com.example.contacts_phase1.data.Contact
import com.example.contacts_phase1.ui.ContactListUiState

/**
 * The home screen: search box with recent searches, favorites filter, A–Z / Z–A sort and the contact list.
 * Its data comes in as [state] and clicks go out through the lambdas (state hoisting); it only remembers
 * small UI details itself: whether the search box is focused, and the list's scroll position.
 * The last three parameters have defaults, so the screen also works before MainActivity passes them.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContactListScreen(
    state: ContactListUiState,
    onQueryChange: (String) -> Unit,
    onToggleSort: () -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit,
    onToggleFavorite: (Contact) -> Unit,
    onContactClick: (Contact) -> Unit,
    onAddClick: () -> Unit,
    onSearch: () -> Unit = {},
    onRemoveRecentSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
) {
    // clearFocus() takes the focus away from the search box: its highlight goes and the keyboard closes.
    val focusManager = LocalFocusManager.current
    var searchFocused by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    // Closing the keyboard with the Back button also leaves the search box.
    val keyboardOpen = WindowInsets.isImeVisible
    LaunchedEffect(keyboardOpen) {
        if (!keyboardOpen) focusManager.clearFocus()
    }
    // So does scrolling the list.
    LaunchedEffect(gridState.isScrollInProgress) {
        if (gridState.isScrollInProgress) focusManager.clearFocus()
    }

    val showRecentSearches = searchFocused && state.query.isBlank() && state.recentSearches.isNotEmpty()

    Scaffold(
        // A tap on any empty part of the screen leaves the search box.
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contacts_title)) },
                actions = {
                    // Shows the current order; tapping switches it.
                    TextButton(
                        onClick = {
                            focusManager.clearFocus()
                            onToggleSort()
                        },
                    ) {
                        Text(
                            stringResource(if (state.sortAscending) R.string.sort_a_to_z else R.string.sort_z_to_a)
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    focusManager.clearFocus()
                    onAddClick()
                },
            ) {
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
                trailingIcon = if (state.query.isNotEmpty()) {
                    {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.clear_search))
                        }
                    }
                } else {
                    null
                },
                singleLine = true,
                // The keyboard shows a Search key; pressing it leaves the box, which saves the search.
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .onFocusChanged { focus ->
                        // Leaving the box (tap outside, Search key, Back, scrolling, opening a contact)
                        // adds what was typed to the recent searches.
                        if (searchFocused && !focus.isFocused) onSearch()
                        searchFocused = focus.isFocused
                    },
            )

            if (showRecentSearches) {
                RecentSearches(
                    searches = state.recentSearches,
                    onSearchClick = { search ->
                        onQueryChange(search)
                        // Leaving the box saves the search again, which moves it to the top.
                        focusManager.clearFocus()
                    },
                    onRemove = onRemoveRecentSearch,
                    onClearAll = onClearRecentSearches,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    FilterChip(
                        selected = state.favoritesOnly,
                        onClick = {
                            focusManager.clearFocus()
                            onFavoritesOnlyChange(!state.favoritesOnly)
                        },
                        label = { Text(stringResource(R.string.favorites_only)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                // Grey while the filter is off; the color of the list's hearts while it is on.
                                tint = if (state.favoritesOnly) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                            )
                        },
                    )
                }

                when {
                    // Show nothing while the first database read runs, so "No contacts yet" does not flash.
                    state.isLoading -> Unit
                    state.contacts.isEmpty() -> EmptyState(
                        message = when {
                            state.query.isNotBlank() -> R.string.no_matches
                            state.favoritesOnly -> R.string.no_favorites
                            else -> R.string.no_contacts
                        },
                    )
                    else -> {
                        val groupNames = state.groups.associate { group -> group.id to group.name }
                        // Adaptive grid: one column on a phone held upright,
                        // two or more in landscape and on tablets (responsive layout).
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 320.dp),
                            state = gridState,
                            contentPadding = PaddingValues(bottom = 88.dp),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(state.contacts, key = { contact -> contact.id }) { contact ->
                                ContactRow(
                                    contact = contact,
                                    groupName = contact.groupId?.let { id -> groupNames[id] },
                                    onClick = {
                                        focusManager.clearFocus()
                                        onContactClick(contact)
                                    },
                                    onToggleFavorite = {
                                        focusManager.clearFocus()
                                        onToggleFavorite(contact)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(@StringRes message: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Text(
            text = stringResource(message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
