package com.jackapps.batteryalarm.presentation.settings_screen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SliderSetting(
    text: String,
    value: Float,
    icon: ImageVector,
    enabled: Boolean = true,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = text) },
        supportingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    enabled = enabled,
                    value = value,
                    valueRange = 0f..100f,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    onValueChangeFinished = onValueChangeFinished
                )

                Spacer(Modifier.width(4.dp))

                Text(text = "${value.toInt()}%")
            }
        },
        leadingContent = { SettingsIcon(icon = icon) }
    )
}
