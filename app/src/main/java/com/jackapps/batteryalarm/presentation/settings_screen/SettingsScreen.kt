package com.jackapps.batteryalarm.presentation.settings_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jackapps.batteryalarm.BuildConfig
import com.jackapps.batteryalarm.model.PreferencesKeys
import com.jackapps.batteryalarm.model.Theme
import com.jackapps.batteryalarm.model.format
import com.jackapps.batteryalarm.presentation.components.AppBar
import com.jackapps.batteryalarm.presentation.settings_screen.components.*
import com.jackapps.batteryalarm.presentation.util.Navigator.popBackStack
import com.jackapps.batteryalarm.presentation.util.Screens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    activity: SettingsActivity,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val scrollState = rememberScrollState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var batteryThreshold by remember { mutableStateOf(state.batteryThreshold.toFloat()) }
    var volumeLevel by remember { mutableStateOf(state.volumeLevel.toFloat()) }

    Scaffold(
        topBar = {
            AppBar(
                title = Screens.Settings.title,
                showBack = true,
                onBack = { activity.popBackStack() }
            )
        }
    ) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            SettingSubTitle(text = "General")

            ListItem(
                text = { Text("App Theme") },
                secondaryText = { Text(state.theme.format()) },
                modifier = Modifier.clickable { showThemeDialog = true },
                icon = { SettingsIcon(icon = Icons.Default.Palette) }
            )

            SliderSetting(
                text = "Battery Threshold",
                value = batteryThreshold,
                onValueChange = { value -> batteryThreshold = value },
                icon = Icons.Default.BatteryChargingFull,
                onValueChangeFinished = {
                    viewModel.setPreferences(
                        PreferencesKeys.BATTERY_THRESHOLD,
                        batteryThreshold.toInt()
                    )
                }
            )

            CheckBoxSetting(
                text = "Should Vibrate",
                checked = state.shouldVibrate,
                icon = Icons.Default.Vibration
            ) {
                viewModel.setPreferences(PreferencesKeys.SHOULD_VIBRATE, !state.shouldVibrate)
            }

            CheckBoxSetting(
                text = "Should Sound",
                checked = state.shouldSound,
                icon = Icons.Default.NotificationsActive
            ) {
                viewModel.setPreferences(PreferencesKeys.SHOULD_SOUND, !state.shouldSound)
            }

            SliderSetting(
                text = "Volume Level",
                value = volumeLevel,
                enabled = state.shouldSound,
                onValueChange = { value -> volumeLevel = value },
                icon = Icons.Default.VolumeUp,
                onValueChangeFinished = {
                    viewModel.setPreferences(
                        PreferencesKeys.VOLUME_LEVEL,
                        volumeLevel.toInt()
                    )
                }
            )

            SettingSubTitle(text = "About")

            ListItem(
                text = { Text("Version") },
                secondaryText = { Text(BuildConfig.VERSION_NAME) },
                modifier = Modifier.clickable { },
                icon = { SettingsIcon(icon = Icons.Default.Tag) }
            )

            ListItem(
                text = { Text("Developed By") },
                secondaryText = { Text("Jagadeesh") },
                modifier = Modifier.clickable { },
                icon = { SettingsIcon(icon = Icons.Default.Build) }
            )
        }
    }

    if (showThemeDialog) {
        DialogWithOptions(
            title = "Select Theme",
            options = Theme.values().toList().map { it.format() },
            selectedIndex = state.theme.ordinal,
            onSelect = { index ->
                viewModel.setPreferences(PreferencesKeys.THEME, Theme.values()[index].name)
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}
