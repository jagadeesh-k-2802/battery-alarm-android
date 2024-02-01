package com.jackapps.batteryalarm.presentation.settings_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CheckBoxSetting(
    text: String,
    enabled: Boolean = true,
    checked: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        text = { Text(text = text) },
        modifier = Modifier.clickable(onClick = onClick),
        icon = { SettingsIcon(icon = icon) },
        trailing = {
            Checkbox(
                checked = checked,
                enabled = enabled,
                onCheckedChange = { onClick() }
            )
        }
    )
}
