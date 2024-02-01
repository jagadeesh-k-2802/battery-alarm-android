package com.jackapps.batteryalarm.presentation.home_screen

data class HomeState(
    val isServiceRunning: Boolean,
    val batteryThreshold: Int = 89
)
