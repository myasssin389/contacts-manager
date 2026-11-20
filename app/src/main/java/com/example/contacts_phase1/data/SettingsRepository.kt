package com.example.contacts_phase1.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// One DataStore file ("settings") for the whole app.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Small settings that must survive closing the app, saved with Preferences DataStore
 * (the modern replacement for SharedPreferences from Lab 4): the sort order and the recent searches.
 */
class SettingsRepository(private val context: Context) {

    private val sortAscendingKey = booleanPreferencesKey("sort_ascending")
    private val recentSearchesKey = stringPreferencesKey("recent_searches")

    // If the settings file cannot be read, carry on with the default settings instead of crashing.
    private val storedPreferences: Flow<Preferences> = context.dataStore.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    /** true = A to Z (the default), false = Z to A. */
    val sortAscending: Flow<Boolean> =
        storedPreferences.map { preferences -> preferences[sortAscendingKey] ?: true }

    /** The last few searches, newest first. */
    val recentSearches: Flow<List<String>> =
        storedPreferences.map { preferences -> preferences.readRecentSearches() }

    /** Flips A–Z / Z–A inside one edit, so two quick taps cannot both read the same old value. */
    suspend fun toggleSortOrder() {
        context.dataStore.edit { preferences ->
            preferences[sortAscendingKey] = !(preferences[sortAscendingKey] ?: true)
        }
    }

    /** Puts [text] at the top of the recent searches. An older copy (in any letter case) is removed. */
    suspend fun addRecentSearch(text: String) {
        // Keep it on one line: a pasted line break would otherwise split one search into two.
        val search = text.trim().replace(WHITESPACE, " ")
        if (search.isEmpty()) return
        context.dataStore.edit { preferences ->
            val older = preferences.readRecentSearches().filterNot { it.equals(search, ignoreCase = true) }
            preferences[recentSearchesKey] =
                (listOf(search) + older).take(MAX_RECENT_SEARCHES).joinToString(SEPARATOR)
        }
    }

    suspend fun removeRecentSearch(text: String) {
        context.dataStore.edit { preferences ->
            preferences[recentSearchesKey] =
                preferences.readRecentSearches().filterNot { it == text }.joinToString(SEPARATOR)
        }
    }

    suspend fun clearRecentSearches() {
        context.dataStore.edit { preferences -> preferences.remove(recentSearchesKey) }
    }

    // Preferences DataStore has no list type, so the searches are kept as one text, one search per line.
    private fun Preferences.readRecentSearches(): List<String> =
        this[recentSearchesKey].orEmpty().split(SEPARATOR).filter { it.isNotBlank() }.distinct()

    private companion object {
        const val SEPARATOR = "\n"
        const val MAX_RECENT_SEARCHES = 5
        val WHITESPACE = Regex("\\s+")
    }
}
