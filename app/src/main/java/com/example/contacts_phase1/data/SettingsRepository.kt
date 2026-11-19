package com.example.contacts_phase1.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// One DataStore file ("settings") for the whole app.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Small settings that must survive closing the app, saved with Preferences DataStore
 * (the modern replacement for SharedPreferences from Lab 4).
 */
class SettingsRepository(private val context: Context) {

    private val sortAscendingKey = booleanPreferencesKey("sort_ascending")

    /** true = A to Z (the default), false = Z to A. */
    val sortAscending: Flow<Boolean> =
        context.dataStore.data.map { preferences -> preferences[sortAscendingKey] ?: true }

    suspend fun setSortAscending(ascending: Boolean) {
        context.dataStore.edit { preferences -> preferences[sortAscendingKey] = ascending }
    }
}
