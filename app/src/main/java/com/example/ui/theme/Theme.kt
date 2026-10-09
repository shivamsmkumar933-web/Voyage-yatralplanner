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

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOceanNight,
    onPrimary = Color(0xFF00344D),
    primaryContainer = PrimaryOceanContainerNight,
    onPrimaryContainer = Color(0xFFC7E7FF),
    secondary = SecondaryCoralNight,
    onSecondary = Color(0xFF532100),
    secondaryContainer = SecondaryCoralContainerNight,
    onSecondaryContainer = Color(0xFFFFDBCB),
    tertiary = TertiaryEmeraldNight,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOcean,
    onPrimary = Color.White,
    primaryContainer = PrimaryOceanLight,
    onPrimaryContainer = PrimaryOceanDark,
    secondary = SecondaryCoral,
    onSecondary = Color.White,
    secondaryContainer = SecondaryCoralLight,
    onSecondaryContainer = Color(0xFF9A3412),
    tertiary = TertiaryEmerald,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    outline = OutlineLight
)

@Composable
fun VoyageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional travel branding palette
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
