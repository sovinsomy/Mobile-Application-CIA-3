package com.example.taskmanager.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TaskManagerColorScheme = darkColorScheme(
    primary = PurpleAccent,
    onPrimary = Color.Black,
    primaryContainer = PurpleDark,
    onPrimaryContainer = TextWhite,
    secondary = CyanAccent,
    onSecondary = Color.Black,
    secondaryContainer = CyanDark,
    onSecondaryContainer = TextWhite,
    tertiary = NeonPink,
    onTertiary = Color.White,
    tertiaryContainer = NeonPinkDark,
    onTertiaryContainer = TextWhite,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextGrey,
    error = ErrorRed,
    onError = Color.Black,
    outline = TextDimGrey,
    inverseSurface = TextWhite,
    inverseOnSurface = DarkBackground,
)

@Composable
fun TaskManagerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = TaskManagerColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBackground.toArgb()
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}