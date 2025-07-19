package com.jackapps.batteryalarm.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.jackapps.batteryalarm.domain.PreferencesRepository
import com.jackapps.batteryalarm.model.AppPreferences
import com.jackapps.batteryalarm.model.PreferencesKeys
import com.jackapps.batteryalarm.model.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    override val preferencesFlow: Flow<AppPreferences>
        get() = dataStore.data
            .catch { exception ->
                // dataStore.data throws an IOException if it can't read the data
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val theme = preferences[PreferencesKeys.THEME] ?: Theme.SYSTEM_DEFAULT.name
                val batteryThreshold = preferences[PreferencesKeys.BATTERY_THRESHOLD] ?: 95
                val shouldVibrate = preferences[PreferencesKeys.SHOULD_VIBRATE] ?: true
                val shouldSound = preferences[PreferencesKeys.SHOULD_SOUND] ?: true
                val volumeLevel = preferences[PreferencesKeys.VOLUME_LEVEL] ?: 100

                AppPreferences(
                    Theme.valueOf(theme),
                    batteryThreshold,
                    shouldVibrate,
                    shouldSound,
                    volumeLevel
                )
            }

    override suspend fun <T> setPreferences(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences -> preferences[key] = value }
    }
}
