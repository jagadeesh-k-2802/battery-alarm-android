package com.jackapps.batteryalarm.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jackapps.batteryalarm.model.Theme

private val DarkColorScheme = darkColorScheme(
    primary = Purple200,
    secondary = Purple700,
    tertiary = Teal200
)

private val LightColorScheme = lightColorScheme(
    primary = Purple500,
    secondary = Purple700,
    tertiary = Teal200
)

@Composable
fun BatteryAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    viewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    // Dynamic color is available on Android 12+
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val theme by viewModel.theme

    val colorScheme = when (theme) {
        Theme.LIGHT -> {
            if (dynamicColor) dynamicLightColorScheme(context) else LightColorScheme
        }
        Theme.DARK -> {
            if (dynamicColor) dynamicDarkColorScheme(context) else DarkColorScheme
        }
        Theme.SYSTEM_DEFAULT -> {
            if (darkTheme) {
                if (dynamicColor) dynamicDarkColorScheme(context) else DarkColorScheme
            } else {
                if (dynamicColor) dynamicLightColorScheme(context) else LightColorScheme
            }
        }
    }

    systemUiController.setSystemBarsColor(
        colorScheme.surface,
        darkIcons = theme == Theme.LIGHT || theme == Theme.SYSTEM_DEFAULT && !darkTheme
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
