package com.jackapps.batteryalarm.presentation.home_screen

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.jackapps.batteryalarm.presentation.components.AppBar
import com.jackapps.batteryalarm.presentation.theme.layoutPadding
import com.jackapps.batteryalarm.presentation.util.Navigator.navigate
import com.jackapps.batteryalarm.presentation.util.Screens
import com.jackapps.batteryalarm.presentation.util.isAndroid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    activity: MainActivity,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val isServiceRunning = state.isServiceRunning
    val batteryThreshold = state.batteryThreshold
    val height = LocalConfiguration.current.screenHeightDp.dp

    val notificationPermissionState =
        if (isAndroid(Build.VERSION_CODES.TIRAMISU)) {
            rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            null
        }

    LaunchedEffect(Unit) {
        notificationPermissionState?.launchPermissionRequest()
    }

    Scaffold(
        topBar = {
            AppBar(title = Screens.Home.title) {
                IconButton(onClick = { activity.navigate(Screens.Settings) }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(layoutPadding)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(height / 6))

                Box(modifier = Modifier.size(180.dp)) {
                    Icon(
                        imageVector = if (isServiceRunning)
                            Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = "Service not running.",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    text = if (isServiceRunning) "Alarm will activate at $batteryThreshold%"
                    else "Alarm Disabled",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp),
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = { viewModel.onEvent(HomeEvents.ToggleService) },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp)
                )

                Text(
                    text = "${if (isServiceRunning) "Disable" else "Enable"} Service".uppercase()
                )
            }
        }
    }
}
