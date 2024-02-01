package com.jackapps.batteryalarm.presentation.home_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jackapps.batteryalarm.presentation.components.AppBar
import com.jackapps.batteryalarm.presentation.theme.layoutPadding
import com.jackapps.batteryalarm.presentation.util.Navigator.navigate
import com.jackapps.batteryalarm.presentation.util.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activity: MainActivity,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val isServiceRunning = state.isServiceRunning
    val batteryThreshold = state.batteryThreshold
    val height = LocalConfiguration.current.screenHeightDp.dp

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
