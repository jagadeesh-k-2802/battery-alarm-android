package com.jackapps.batteryalarm.presentation.settings_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CheckBoxSetting(
    text: String,
    enabled: Boolean = true,
    checked: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = text) },
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = { SettingsIcon(icon = icon) },
        trailingContent = {
            Checkbox(
                checked = checked,
                enabled = enabled,
                onCheckedChange = { onClick() }
            )
        }
    )
}
