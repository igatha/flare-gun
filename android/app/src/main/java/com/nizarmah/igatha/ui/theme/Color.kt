package com.nizarmah.igatha.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Gray = Color(0xFF8E8E93)

// iOS colors
// @see https://developer.apple.com/design/human-interface-guidelines/color#iOS-iPadOS-system-colors

val RedLight = Color(255, 59, 48)
val RedDark = Color(255, 69, 58)

val PinkLight = Color(255, 45, 85)
val PinkDark = Color(255, 55, 95)

val PurpleLight = Color(175, 82, 222)
val PurpleDark = Color(191, 90, 242)

/**
 * Data class representing iOS color palette with light/dark variants
 */
data class ColorScheme(
    val red: Color = RedLight,
    val pink: Color = PinkLight,
    val purple: Color = PurpleLight
)

/**
 * Light mode iOS colors
 */
val LightColors = ColorScheme(
    red = RedLight,
    pink = PinkLight,
    purple = PurpleLight
)

/**
 * Dark mode iOS colors
 */
val DarkColors = ColorScheme(
    red = RedDark,
    pink = PinkDark,
    purple = PurpleDark
)

/**
 * CompositionLocal to provide iOS colors down the tree
 */
val LocalColors = staticCompositionLocalOf { LightColors }

/**
 * Extension property for accessing iOS colors from MaterialTheme
 */
val MaterialTheme.colors: ColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current
