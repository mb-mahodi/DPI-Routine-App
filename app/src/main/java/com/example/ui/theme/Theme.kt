package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BoldBlack,
    onPrimary = PureWhite,
    primaryContainer = SoftBackground,
    onPrimaryContainer = DarkText,
    secondary = PastelBlue,
    onSecondary = PastelBlueText,
    secondaryContainer = PastelBlue,
    onSecondaryContainer = PastelBlueText,
    tertiary = PastelPurple,
    onTertiary = PastelPurpleText,
    background = SoftBackground,
    onBackground = DarkText,
    surface = PureWhite,
    onSurface = DarkText,
    surfaceVariant = SoftBackground,
    onSurfaceVariant = MutedText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
