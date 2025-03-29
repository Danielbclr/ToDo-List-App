package com.danbramos.todolist.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danbramos.todolist.viewmodel.SettingsViewModel
import com.danbramos.todolist.viewmodel.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),           // Light blue for primary actions
    onPrimary = Color.White,
    secondary = Color(0xFF81D4FA),         // Lighter blue for secondary elements
    onSecondary = Color.White,
    tertiary = Color(0xFF80DEEA),          // Turquoise accent
    onTertiary = Color.White,
    background = DarkModeBg,               // Very dark blue background
    onBackground = Color(0xFFE0E0E0),      // Light gray text on background
    surface = DarkModeSurface,             // Slightly lighter dark blue for surfaces
    onSurface = Color(0xFFE0E0E0),         // Light gray text on surfaces
    error = Color(0xFFCF6679),             // Reddish error color
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF110C00),           // Orange for primary actions
    onPrimary = Color.White,
    secondary = Color(0xFFA82700),         // Light orange for secondary elements
    onSecondary = Color(0xFF333333),
    tertiary = Color(0xFFFFD54F),          // Yellow accent
    onTertiary = Color(0xFF333333),
    background = LightModeBg,              // Pale orange/off-white background
    onBackground = Color(0xFF333333),      // Dark gray text on background
    surface = LightModeSurface,            // Slightly more saturated surface
    onSurface = Color(0xFF100202),         // Dark gray text on surfaces
    error = Color(0xFFB00020),             // Standard error color
    onError = Color.White
)

@Composable
fun TodoListTheme(
    settingsViewModel: SettingsViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val themeMode = settingsViewModel.themeMode.collectAsState().value
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    val dynamicColor = false // Disable dynamic color to use our custom colors
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}