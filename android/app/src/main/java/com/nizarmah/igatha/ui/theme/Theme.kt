package com.nizarmah.igatha.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val BaseLightColorScheme = lightColorScheme()

private val BaseDarkColorScheme = darkColorScheme()

private val LightColorScheme = BaseLightColorScheme.copy(
    primary = RedLight,
)

private val DarkColorScheme = BaseDarkColorScheme.copy(
    primary = RedDark,
)

@Composable
fun IgathaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    // Select the appropriate iOS colors based on theme
    val colors = if (darkTheme) DarkColors else LightColors

    // Provide both Material and iOS colors down the tree
    CompositionLocalProvider(
        LocalColors provides colors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
