package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = OrangeContainer,
    onPrimaryContainer = OrangePrimaryDark,
    secondary = GreenSuccess,
    onSecondary = Color.White,
    secondaryContainer = GreenContainer,
    onSecondaryContainer = GreenSuccessDark,
    background = BackgroundLight,
    onBackground = TextDark,
    surface = SurfaceLight,
    onSurface = TextDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextMuted,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF7C2D12),
    onPrimaryContainer = OrangeContainer,
    secondary = GreenSuccess,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF14532D),
    onSecondaryContainer = GreenContainer,
    background = BackgroundDark,
    onBackground = TextLight,
    surface = SurfaceDark,
    onSurface = TextLight,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextDarkMuted,
    outline = BorderDark
)

@Composable
fun StreaksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
