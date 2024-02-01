package com.jackapps.batteryalarm.presentation.alarm_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jackapps.batteryalarm.domain.PreferencesRepository
import com.jackapps.batteryalarm.model.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _preferences = mutableStateOf(AppPreferences())
    val preferences: State<AppPreferences> = _preferences

    init {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collect { preferences ->
                _preferences.value = preferences
            }
        }
    }
}
