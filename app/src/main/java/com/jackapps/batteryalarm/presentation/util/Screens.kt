package com.jackapps.batteryalarm.presentation.util

sealed class Screens(val title: String) {
    object Home : Screens(title = "Battery Alarm")
    object Settings : Screens(title = "Settings")
}
