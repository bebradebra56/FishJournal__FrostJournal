package com.fishjorunal.sofircl.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val SORT_ORDER = stringPreferencesKey("sort_order")
    }
    
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }
    
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }
    
    val sortOrder: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SORT_ORDER] ?: "date_desc"
        }
    
    suspend fun setSortOrder(order: String) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ORDER] = order
        }
    }
}
