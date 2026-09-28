package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val AnimeDarkColorScheme = darkColorScheme(
    primary = AnimePrimary,
    onPrimary = Color.White,
    primaryContainer = AnimePrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = AnimeSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D5A),
    onSecondaryContainer = Color.White,
    tertiary = AnimeTertiary,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder,
    outlineVariant = Color(0xFF2C394F)
)

private val AnimeLightColorScheme = lightColorScheme(
    primary = AnimePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E2),
    onPrimaryContainer = Color(0xFF3E0013),
    secondary = Color(0xFF00677D),
    onSecondary = Color.White,
    tertiary = AnimeTertiary,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun AnimeHubTheme(
    darkTheme: Boolean = true, // Default to Dark mode as requested
    dynamicColor: Boolean = false, // Keep branded anime colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AnimeDarkColorScheme else AnimeLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
