package com.firoeu.dietie.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand palette taken from the original Dietrix web app
// primary #6A4BD4 (purple), secondary #1F6FC2 (blue), tertiary #2F9464 (green)

val LightColors = lightColorScheme(
    primary = Color(0xFF6A4BD4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE7DEFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF1F6FC2),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD3E4FF),
    onSecondaryContainer = Color(0xFF001C3A),
    tertiary = Color(0xFF2F9464),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFB5F1CE),
    onTertiaryContainer = Color(0xFF00210F),
    error = Color(0xFFB4423A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFCE9E7),
    onErrorContainer = Color(0xFF6E211B),
    background = Color(0xFFF6F3FF),
    onBackground = Color(0xFF23222B),
    surface = Color(0xFFFDF8FF),
    onSurface = Color(0xFF23222B),
    surfaceVariant = Color(0xFFE9E2F5),
    onSurfaceVariant = Color(0xFF63616F),
    outline = Color(0xFF7B7887),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFB9A5F5),
    onPrimary = Color(0xFF32148F),
    primaryContainer = Color(0xFF5133B3),
    onPrimaryContainer = Color(0xFFE7DEFF),
    secondary = Color(0xFF5E9FE8),
    onSecondary = Color(0xFF00315F),
    secondaryContainer = Color(0xFF004884),
    onSecondaryContainer = Color(0xFFD3E4FF),
    tertiary = Color(0xFF72BC8F),
    onTertiary = Color(0xFF00391E),
    tertiaryContainer = Color(0xFF13512F),
    onTertiaryContainer = Color(0xFFB5F1CE),
    error = Color(0xFFF0968C),
    onError = Color(0xFF5A1710),
    errorContainer = Color(0xFF7D2B23),
    onErrorContainer = Color(0xFFFCE9E7),
    background = Color(0xFF12101B),
    onBackground = Color(0xFFF4F4F6),
    surface = Color(0xFF16141F),
    onSurface = Color(0xFFF4F4F6),
    surfaceVariant = Color(0xFF2A2735),
    onSurfaceVariant = Color(0xFFCBC7D6),
    outline = Color(0xFF958FA3),
)
