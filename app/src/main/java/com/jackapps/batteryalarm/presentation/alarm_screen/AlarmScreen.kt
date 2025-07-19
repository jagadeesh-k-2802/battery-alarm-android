package com.jackapps.batteryalarm.presentation.alarm_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jackapps.batteryalarm.R
import com.jackapps.batteryalarm.presentation.theme.layoutPadding

@Composable
fun AlarmScreen(
    onDismiss: () -> Unit,
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences
    val height = LocalConfiguration.current.screenHeightDp.dp

    Scaffold { contentPadding ->
        Box(modifier = Modifier
            .padding(contentPadding)
            .padding(layoutPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(height / 10))

                Text(
                    text = stringResource(R.string.text_battery_alarm),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.text_charged_to, preferences.batteryThreshold),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    text = stringResource(R.string.text_unplug_dismiss),
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Text(text = stringResource(R.string.text_dismiss))
            }
        }
    }
}
