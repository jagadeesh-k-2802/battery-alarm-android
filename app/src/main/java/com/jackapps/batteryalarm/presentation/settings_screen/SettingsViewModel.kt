package com.jackapps.batteryalarm.presentation.settings_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jackapps.batteryalarm.domain.PreferencesRepository
import com.jackapps.batteryalarm.model.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _state = mutableStateOf(AppPreferences())
    val state: State<AppPreferences> = _state

    init {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collect { preferences ->
                _state.value = preferences
            }
        }
    }

    fun <T> setPreferences(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            preferencesRepository.setPreferences(key, value)
        }
    }
}
