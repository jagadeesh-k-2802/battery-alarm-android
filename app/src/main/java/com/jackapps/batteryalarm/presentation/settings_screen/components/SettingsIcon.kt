package com.jackapps.batteryalarm.presentation.settings_screen.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SettingsIcon(icon: ImageVector, size: Dp = 30.dp) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(size)
    )
}
