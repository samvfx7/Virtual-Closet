package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = ObsidianBg,
    primaryContainer = DarkNavyElevated,
    onPrimaryContainer = ChampagneGoldLight,
    secondary = RoseGold,
    onSecondary = ObsidianBg,
    secondaryContainer = DarkNavyElevated,
    onSecondaryContainer = RoseGold,
    tertiary = AmberGlow,
    onTertiary = ObsidianBg,
    background = ObsidianBg,
    onBackground = PlatinumText,
    surface = DarkNavySurface,
    onSurface = PlatinumText,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = MutedText,
    outline = CardBorderGold,
    outlineVariant = SurfaceBorder
)

private val LuxuryLightColorScheme = lightColorScheme(
    primary = ChampagneGoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF8E7),
    onPrimaryContainer = ChampagneGoldDark,
    secondary = RoseGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECE8),
    onSecondaryContainer = Color(0xFF6E281F),
    background = Color(0xFFF8FAFC),
    onBackground = DarkText,
    surface = Color.White,
    onSurface = DarkText,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun VirtualClosetTheme(
    darkTheme: Boolean = true, // Luxury dark by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LuxuryDarkColorScheme else LuxuryLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
