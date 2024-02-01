package com.jackapps.batteryalarm.presentation.home_screen

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jackapps.batteryalarm.domain.PreferencesRepository
import com.jackapps.batteryalarm.services.BatteryAlarmService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _state = mutableStateOf(
        HomeState(
            isServiceRunning = BatteryAlarmService.isServiceRunning(application)
        )
    )

    val state: State<HomeState> = _state

    /* When service is started updates the state */
    private val serviceStartedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            _state.value = _state.value.copy(isServiceRunning = true)
        }
    }

    /* When service is stopped updates the state */
    private val serviceStoppedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            _state.value = _state.value.copy(isServiceRunning = false)
        }
    }

    init {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collect { preferences ->
                _state.value = _state.value.copy(batteryThreshold = preferences.batteryThreshold)
            }
        }

        application.registerReceiver(
            serviceStartedReceiver,
            IntentFilter(BatteryAlarmService.ACTION_STARTED)
        )

        application.registerReceiver(
            serviceStoppedReceiver,
            IntentFilter(BatteryAlarmService.ACTION_STOPPED)
        )
    }

    fun onEvent(event: HomeEvents) {
        when (event) {
            HomeEvents.ToggleService -> {
                BatteryAlarmService.toggleService(application)
            }
        }
    }

    override fun onCleared() {
        application.unregisterReceiver(serviceStartedReceiver)
        application.unregisterReceiver(serviceStoppedReceiver)
        super.onCleared()
    }
}
