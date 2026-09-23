package com.fahim.mad_lab_compose.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GeminiPrimaryDark,
    onPrimary = GeminiOnPrimaryDark,
    primaryContainer = GeminiPrimaryContainerDark,
    onPrimaryContainer = GeminiOnPrimaryContainerDark,
    secondary = GeminiSecondaryDark,
    onSecondary = GeminiOnSecondaryDark,
    secondaryContainer = GeminiSecondaryContainerDark,
    onSecondaryContainer = GeminiOnSecondaryContainerDark,
    background = GeminiBackgroundDark,
    surface = GeminiSurfaceDark,
    surfaceContainer = GeminiSurfaceContainerDark,
    surfaceContainerHigh = GeminiSurfaceContainerHighDark
)

private val LightColorScheme = lightColorScheme(
    primary = GeminiPrimaryLight,
    onPrimary = GeminiOnPrimaryLight,
    primaryContainer = GeminiPrimaryContainerLight,
    onPrimaryContainer = GeminiOnPrimaryContainerLight,
    secondary = GeminiSecondaryLight,
    onSecondary = GeminiOnSecondaryLight,
    secondaryContainer = GeminiSecondaryContainerLight,
    onSecondaryContainer = GeminiOnSecondaryContainerLight,
    background = GeminiBackgroundLight,
    surface = GeminiSurfaceLight,
    surfaceContainer = GeminiSurfaceContainerLight,
    surfaceContainerHigh = GeminiSurfaceContainerHighLight
)

@Composable
fun GeminiChatbotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}