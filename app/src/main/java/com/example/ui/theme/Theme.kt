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

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = PrimaryPurpleLight,
    onPrimaryContainer = PrimaryPurpleText,
    secondary = NeutralMedium,
    onSecondary = Color.White,
    secondaryContainer = NeutralContainer,
    onSecondaryContainer = NeutralDark,
    tertiary = AccentBlue,
    onTertiary = Color.White,
    tertiaryContainer = AccentBlueContainer,
    background = AppBackground,
    onBackground = NeutralDark,
    surface = Color.White,
    onSurface = NeutralDark,
    surfaceVariant = NeutralContainer,
    onSurfaceVariant = NeutralMedium,
    outline = NeutralBorder,
    outlineVariant = TopBarBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF382352),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF2B2833),
    onSecondaryContainer = Color(0xFFE6E0E9),
    tertiary = Color(0xFFA1C9FF),
    onTertiary = Color(0xFF00325A),
    tertiaryContainer = Color(0xFF182E44),
    background = Color(0xFF141218),
    onBackground = Color(0xFFF0EAEF),
    surface = Color(0xFF1E1B24),
    onSurface = Color(0xFFF0EAEF),
    surfaceVariant = Color(0xFF2B2735),
    onSurfaceVariant = Color(0xFFD0C9DB),
    outline = Color(0xFF49454F),
    outlineVariant = Color(0xFF322E3A)
)

@Composable
fun CepHesapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to maintain our bespoke High Density design palette
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
