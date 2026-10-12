package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SiakadDarkColorScheme = darkColorScheme(
    primary = IndigoAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = AmberAcademic,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF451A03),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = EmeraldSuccess,
    onTertiary = Color(0xFF052E16),
    background = NavyDeep,
    onBackground = SlateTextPrimary,
    surface = NavySurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = NavyCard,
    onSurfaceVariant = SlateTextSecondary,
    error = RoseAlert,
    onError = Color.White
)

@Composable
fun SiakadCloudTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SiakadDarkColorScheme,
        typography = Typography,
        content = content
    )
}
