package com.jackapps.batteryalarm.model

data class AppPreferences(
    val theme: Theme = Theme.SYSTEM_DEFAULT,
    val batteryThreshold: Int = 95,
    val shouldVibrate: Boolean = true,
    val shouldSound: Boolean = true,
    val volumeLevel: Int = 100
)
