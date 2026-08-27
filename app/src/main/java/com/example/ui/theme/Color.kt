package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// HIGH-END DIGITAL LUXURY COLOR SYSTEM
// Linear / Arc / Fintech Craft Standard
// ==========================================

// Canvas Base
val PureBlack = Color(0xFF000000)
val ObsidianBg = Color(0xFF000000)

// Layered Physical Surfaces
val SurfaceLevel0 = Color(0xFF000000)
val SurfaceLevel1 = Color(0xFF09090B) // Subtle elevation
val SurfaceLevel2 = Color(0xFF111114) // Cards, interactive surfaces
val SurfaceLevel3 = Color(0xFF18181D) // Active panels, modals
val SurfaceGlass = Color(0xEE0B0B0E)  // Glassmorphic dock

// Legacy aliases for backward compatibility
val ObsidianSurface = SurfaceLevel1
val DarkNavySurface = SurfaceLevel1
val DarkNavyElevated = SurfaceLevel2
val DarkNavyCard = SurfaceLevel2
val AtmosphereRadialCenter = Color(0xFF0C141A)

// Neon Blue Accent System (#00D9FF)
val NeonBlue = Color(0xFF00D9FF)
val NeonBlueLight = Color(0xFF67E8F9)
val NeonBlueDark = Color(0xFF0891B2)
val NeonBlueGlow = Color(0x3300D9FF)
val NeonBlueSoftGlow = Color(0x1800D9FF)
val NeonBlueSubtle = Color(0x0F00D9FF)
val NeonBlueBorder = Color(0x2900D9FF)

// Soft Off-White & Typography Palette (No blinding #FFFFFF for body)
val PureWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFFF4F4F6)       // Main text & headlines
val TextSecondary = Color(0xFFA1A1AA)     // Explanations, subheads
val TextMuted = Color(0xFF71717A)         // Section labels (OCCASION, WEATHER)
val TextTertiary = Color(0xFF52525B)      // Micro details & dates
val PlatinumText = TextPrimary
val MutedText = TextSecondary
val MutedTextLight = TextSecondary
val DarkText = PureBlack

// Refined 1px Micro Borders (~10-15% opacity)
val BorderSubtle = Color(0x17FFFFFF)
val BorderSubtleLight = Color(0x28FFFFFF)
val BorderNeonSubtle = Color(0x2E00D9FF)
val BorderNeonActive = Color(0xFF00D9FF)
val CardBorderWhite5 = BorderSubtle
val CardBorderWhite10 = BorderSubtleLight
val CardBorderGold = BorderNeonSubtle
val CardBorderGlow = NeonBlueGlow
val SurfaceBorder = BorderSubtle

// Semantic Indicators
val SuccessEmerald = Color(0xFF10B981)
val CoralRed = Color(0xFFF43F5E)
val DangerBg = Color(0xFF22080D)
val ChampagneGold = NeonBlue
val ChampagneGoldLight = NeonBlueLight
val ChampagneGoldDark = NeonBlueDark
val RoseGold = NeonBlue
val AmberGlow = NeonBlue
val AtmosphereBlue = NeonBlue
val SkyBlue = NeonBlue
val VelvetPurple = NeonBlue
