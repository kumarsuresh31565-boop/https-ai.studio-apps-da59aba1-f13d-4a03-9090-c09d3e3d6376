package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ElegantPrimary,
    onPrimary = ElegantOnPrimary,
    secondary = ElegantSecondary,
    onSecondary = ElegantOnSurface,
    background = ElegantBackground,
    onBackground = ElegantOnBackground,
    surface = ElegantSurface,
    onSurface = ElegantOnSurface,
    surfaceVariant = ElegantSurfaceVariant,
    onSurfaceVariant = ElegantOnSurface,
    outline = ElegantBorder
  )

private val LightColorScheme = DarkColorScheme // Elegant Dark is forced since it is an Elegant Dark app theme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by default
  dynamicColor: Boolean = false, // Set default to false to ensure Custom Elegant Dark color scheme is always applied
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme


  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
