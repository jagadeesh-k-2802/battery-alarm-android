package com.jackapps.batteryalarm.domain

import androidx.datastore.preferences.core.Preferences
import com.jackapps.batteryalarm.model.AppPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val preferencesFlow: Flow<AppPreferences>
    suspend fun <T> setPreferences(key: Preferences.Key<T>, value: T)
}
