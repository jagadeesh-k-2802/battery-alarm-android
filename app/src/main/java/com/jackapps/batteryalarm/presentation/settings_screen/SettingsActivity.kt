package com.jackapps.batteryalarm.presentation.settings_screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jackapps.batteryalarm.presentation.theme.BatteryAlarmTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BatteryAlarmTheme {
                SettingsScreen(activity = this)
            }
        }
    }
}
