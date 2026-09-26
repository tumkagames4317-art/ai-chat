package com.ai.frankenstein.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    primaryContainer = md_theme_dark_primaryContainer,
    secondary = md_theme_dark_secondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    tertiary = md_theme_dark_tertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onPrimary = md_theme_dark_onPrimary,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    onSecondary = md_theme_dark_onSecondary,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    onTertiary = md_theme_dark_onTertiary,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    onBackground = md_theme_dark_onBackground,
    onSurface = md_theme_dark_onSurface,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    surfaceInverse = md_theme_dark_surfaceInverse,
    onSurfaceInverse = md_theme_dark_onSurfaceInverse,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    outline = md_theme_dark_outline,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    primaryContainer = md_theme_light_primaryContainer,
    secondary = md_theme_light_secondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    tertiary = md_theme_light_tertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    background = md_theme_light_background,
    surface = md_theme_light_surface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onPrimary = md_theme_light_onPrimary,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    onSecondary = md_theme_light_onSecondary,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    onTertiary = md_theme_light_onTertiary,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    onBackground = md_theme_light_onBackground,
    onSurface = md_theme_light_onSurface,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    surfaceInverse = md_theme_light_surfaceInverse,
    onSurfaceInverse = md_theme_light_onSurfaceInverse,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    outline = md_theme_light_outline,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

@Composable
fun AIFrankensteinTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Custom theme for Forest Swamp design
@Composable
fun ForestSwampTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) {
        ForestSwampColors(
            bgDeep = bgDeep,
            bgPanel = bgPanel,
            bgPanelRaised = bgPanelRaised,
            borderMoss = borderMoss,
            accentLime = accentLime,
            accentLimeDim = accentLimeDim,
            accentEmerald = accentEmerald,
            accentOlive = accentOlive,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            stateWarning = stateWarning,
            stateError = stateError,
            stateSuccess = stateSuccess
        )
    } else {
        ForestSwampColors(
            bgDeep = bgDeepLight,
            bgPanel = bgPanelLight,
            bgPanelRaised = bgPanelRaisedLight,
            borderMoss = borderMossLight,
            accentLime = accentLimeLight,
            accentLimeDim = accentLimeDim,
            accentEmerald = accentEmerald,
            accentOlive = accentOlive,
            textPrimary = textPrimaryLight,
            textSecondary = textSecondaryLight,
            stateWarning = stateWarning,
            stateError = stateError,
            stateSuccess = stateSuccess
        )
    }

    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}

data class ForestSwampColors(
    val bgDeep: Color,
    val bgPanel: Color,
    val bgPanelRaised: Color,
    val borderMoss: Color,
    val accentLime: Color,
    val accentLimeDim: Color,
    val accentEmerald: Color,
    val accentOlive: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val stateWarning: Color,
    val stateError: Color,
    val stateSuccess: Color
)

// Custom theme object
object ForestTheme {
    val colors: ForestSwampColors
        @Composable get() = ForestSwampColors(
            bgDeep = bgDeep,
            bgPanel = bgPanel,
            bgPanelRaised = bgPanelRaised,
            borderMoss = borderMoss,
            accentLime = accentLime,
            accentLimeDim = accentLimeDim,
            accentEmerald = accentEmerald,
            accentOlive = accentOlive,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            stateWarning = stateWarning,
            stateError = stateError,
            stateSuccess = stateSuccess
        )
}
