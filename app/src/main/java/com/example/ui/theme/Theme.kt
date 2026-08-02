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

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF9E00FF), // Neon vibrant violet purple
    secondary = Color(0xFFFFB300), // Glowing gold amber
    tertiary = Color(0xFF00E5FF), // Glowing cyan
    background = KidsDarkBg,
    surface = KidsDarkSurface,
    onBackground = KidsDarkText,
    onSurface = KidsDarkText,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF25114E),
    onPrimaryContainer = Color(0xFFF3E5F5),
    secondaryContainer = Color(0xFF3E3106),
    onSecondaryContainer = Color(0xFFFFF8E1),
    surfaceVariant = Color(0xFF24163E),
    onSurfaceVariant = Color(0xFFE1BEE7)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Purple40,
    secondary = BubblegumPink,
    tertiary = SunnyYellow,
    background = Color(0xFFFFFDF6), // Beautiful buttery cream white for kids warmth
    surface = Color(0xFFFFFAF0),    // Softer warm card background
    onBackground = Color(0xFF2D251D),
    onSurface = Color(0xFF2D251D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7F6),
    onPrimaryContainer = Color(0xFF4527A0),
    secondaryContainer = Color(0xFFFFF3E0),
    onSecondaryContainer = Color(0xFFE65100),
    surfaceVariant = Color(0xFFF3E5F5),
    onSurfaceVariant = Color(0xFF6A1B9A)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color by default for children so our custom fun colors are preserved
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
