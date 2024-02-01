package com.jackapps.batteryalarm.presentation.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jackapps.batteryalarm.domain.PreferencesRepository
import com.jackapps.batteryalarm.model.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _theme = mutableStateOf(Theme.SYSTEM_DEFAULT)
    val theme: State<Theme> = _theme

    init {
        // Running synchronously because of a theme changing flash
        runBlocking {
            _theme.value = preferencesRepository.preferencesFlow.first().theme
        }

        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collect { preferences ->
                _theme.value = preferences.theme
            }
        }
    }
}
