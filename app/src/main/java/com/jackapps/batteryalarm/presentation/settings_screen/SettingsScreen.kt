package com.jackapps.batteryalarm.presentation.settings_screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jackapps.batteryalarm.BuildConfig
import com.jackapps.batteryalarm.model.PreferencesKeys
import com.jackapps.batteryalarm.model.Theme
import com.jackapps.batteryalarm.model.format
import com.jackapps.batteryalarm.presentation.components.AppBar
import com.jackapps.batteryalarm.presentation.settings_screen.components.CheckBoxSetting
import com.jackapps.batteryalarm.presentation.settings_screen.components.DialogWithOptions
import com.jackapps.batteryalarm.presentation.settings_screen.components.SettingSubTitle
import com.jackapps.batteryalarm.presentation.settings_screen.components.SettingsIcon
import com.jackapps.batteryalarm.presentation.settings_screen.components.SliderSetting
import com.jackapps.batteryalarm.presentation.util.Navigator.popBackStack
import com.jackapps.batteryalarm.presentation.util.Screens

@Composable
fun SettingsScreen(
    activity: SettingsActivity,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showThemeDialog by remember { mutableStateOf(false) }
    var batteryThreshold by remember { mutableFloatStateOf(state.batteryThreshold.toFloat()) }
    var volumeLevel by remember { mutableFloatStateOf(state.volumeLevel.toFloat()) }

    Scaffold(
        topBar = {
            AppBar(
                title = Screens.Settings.title,
                showBack = true,
                onBack = { activity.popBackStack() }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .verticalScroll(scrollState)
        ) {
            SettingSubTitle(text = "General")

            ListItem(
                headlineContent = { Text("App Theme") },
                supportingContent = { Text(state.theme.format()) },
                modifier = Modifier.clickable { showThemeDialog = true },
                leadingContent = { SettingsIcon(icon = Icons.Default.Palette) }
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
                icon = Icons.AutoMirrored.Filled.VolumeUp
            ) {
                viewModel.setPreferences(PreferencesKeys.SHOULD_SOUND, !state.shouldSound)
            }

            ListItem(
                headlineContent = { Text("Notification Settings") },
                modifier = Modifier.clickable {
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        context.startActivity(this)
                    }
                },
                leadingContent = { SettingsIcon(icon = Icons.Default.NotificationsActive) },
                trailingContent = { SettingsIcon(icon = Icons.Default.ChevronRight, size = 24.dp) }
            )

            SliderSetting(
                text = "Volume Level",
                value = volumeLevel,
                enabled = state.shouldSound,
                onValueChange = { value -> volumeLevel = value },
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                onValueChangeFinished = {
                    viewModel.setPreferences(
                        PreferencesKeys.VOLUME_LEVEL,
                        volumeLevel.toInt()
                    )
                }
            )

            SettingSubTitle(text = "About")

            ListItem(
                headlineContent = { Text("Version") },
                supportingContent = { Text(BuildConfig.VERSION_NAME) },
                modifier = Modifier.clickable { },
                leadingContent = { SettingsIcon(icon = Icons.Default.Tag) }
            )

            ListItem(
                headlineContent = { Text("Developed By") },
                supportingContent = { Text("Jagadeesh") },
                modifier = Modifier.clickable { },
                leadingContent = { SettingsIcon(icon = Icons.Default.Build) }
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
