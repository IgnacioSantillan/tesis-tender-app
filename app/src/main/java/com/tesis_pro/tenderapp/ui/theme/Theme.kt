package com.tesis_pro.tenderapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class TenderStatusColors(
    val good: Color,
    val goodContainer: Color,
    val onGoodContainer: Color,
    val warn: Color,
    val warnContainer: Color,
    val onWarnContainer: Color,
    val bad: Color,
    val badContainer: Color,
    val onBadContainer: Color,
)

private val LightStatusColors = TenderStatusColors(
    good = MdLightGood,
    goodContainer = MdLightGoodContainer,
    onGoodContainer = MdLightOnGoodContainer,
    warn = MdLightWarn,
    warnContainer = MdLightWarnContainer,
    onWarnContainer = MdLightOnWarnContainer,
    bad = MdLightBad,
    badContainer = MdLightBadContainer,
    onBadContainer = MdLightOnBadContainer,
)

private val DarkStatusColors = TenderStatusColors(
    good = MdDarkGood,
    goodContainer = MdDarkGoodContainer,
    onGoodContainer = MdDarkOnGoodContainer,
    warn = MdDarkWarn,
    warnContainer = MdDarkWarnContainer,
    onWarnContainer = MdDarkOnWarnContainer,
    bad = MdDarkBad,
    badContainer = MdDarkBadContainer,
    onBadContainer = MdDarkOnBadContainer,
)

private val LightColorScheme: ColorScheme = lightColorScheme(
    primary = MdLightPrimary,
    onPrimary = MdLightOnPrimary,
    primaryContainer = MdLightPrimaryContainer,
    onPrimaryContainer = MdLightOnPrimaryContainer,
    background = MdLightBackground,
    onBackground = MdLightOnSurface,
    surface = MdLightSurface,
    onSurface = MdLightOnSurface,
    surfaceVariant = MdLightSurfaceContainer,
    onSurfaceVariant = MdLightOnSurfaceVariant,
    surfaceContainer = MdLightSurfaceContainer,
    surfaceContainerHigh = MdLightSurfaceContainerHigh,
    outline = MdLightOutline,
    outlineVariant = MdLightOutline,
    error = MdLightBad,
    errorContainer = MdLightBadContainer,
    onErrorContainer = MdLightOnBadContainer,
)

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = MdDarkPrimary,
    onPrimary = MdDarkOnPrimary,
    primaryContainer = MdDarkPrimaryContainer,
    onPrimaryContainer = MdDarkOnPrimaryContainer,
    background = MdDarkBackground,
    onBackground = MdDarkOnSurface,
    surface = MdDarkSurface,
    onSurface = MdDarkOnSurface,
    surfaceVariant = MdDarkSurfaceContainer,
    onSurfaceVariant = MdDarkOnSurfaceVariant,
    surfaceContainer = MdDarkSurfaceContainer,
    surfaceContainerHigh = MdDarkSurfaceContainerHigh,
    outline = MdDarkOutline,
    outlineVariant = MdDarkOutline,
    error = MdDarkBad,
    errorContainer = MdDarkBadContainer,
    onErrorContainer = MdDarkOnBadContainer,
)

private val LocalTenderStatusColors = staticCompositionLocalOf { LightStatusColors }

object TenderTheme {
    val statusColors: TenderStatusColors
        @Composable @ReadOnlyComposable get() = LocalTenderStatusColors.current
}

@Composable
fun TenderAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
    val statusColors = if (darkTheme) DarkStatusColors else LightStatusColors

    CompositionLocalProvider(LocalTenderStatusColors provides statusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TenderTypography,
            shapes = TenderShapes,
            content = content
        )
    }
}
