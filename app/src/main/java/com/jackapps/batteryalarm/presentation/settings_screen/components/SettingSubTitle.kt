package com.jackapps.batteryalarm.presentation.settings_screen.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jackapps.batteryalarm.presentation.theme.layoutPadding

@Composable
fun SettingSubTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(layoutPadding)
    )
}
