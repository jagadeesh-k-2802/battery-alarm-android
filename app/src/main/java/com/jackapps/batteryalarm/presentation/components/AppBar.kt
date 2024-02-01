package com.jackapps.batteryalarm.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun AppBar(
    title: String,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    var backButton: (@Composable () -> Unit) = { }

    if (showBack) {
        backButton = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
        }
    }

    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = backButton,
        actions = actions
    )
}
