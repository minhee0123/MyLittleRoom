package com.mylittleroom.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    // Primary: Soft Pink
    primary = Pink40,
    onPrimary = Pink99,
    primaryContainer = Pink90,
    onPrimaryContainer = Pink10,

    // Secondary: Lavender
    secondary = Lavender40,
    onSecondary = Lavender99,
    secondaryContainer = Lavender90,
    onSecondaryContainer = Lavender10,

    // Tertiary: Creamy Mint
    tertiary = Mint40,
    onTertiary = Mint99,
    tertiaryContainer = Mint90,
    onTertiaryContainer = Mint10,

    // Background & Surface
    background = Pink99,
    onBackground = WarmGray10,
    surface = Pink99,
    onSurface = WarmGray10,
    surfaceVariant = Pink95,
    onSurfaceVariant = WarmGray30,

    // Outline
    outline = WarmGray50,
    outlineVariant = WarmGray80,

    // Inverse
    inverseSurface = WarmGray20,
    inverseOnSurface = WarmGray95,
    inversePrimary = Pink80,

    // Error
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,

    // Surface tint
    surfaceTint = Pink40
)

private val DarkColorScheme = darkColorScheme(
    // Primary: Soft Pink
    primary = Pink80,
    onPrimary = Pink20,
    primaryContainer = Pink30,
    onPrimaryContainer = Pink90,

    // Secondary: Lavender
    secondary = Lavender80,
    onSecondary = Lavender20,
    secondaryContainer = Lavender30,
    onSecondaryContainer = Lavender90,

    // Tertiary: Creamy Mint
    tertiary = Mint80,
    onTertiary = Mint20,
    tertiaryContainer = Mint30,
    onTertiaryContainer = Mint90,

    // Background & Surface
    background = WarmGray10,
    onBackground = WarmGray90,
    surface = WarmGray10,
    onSurface = WarmGray90,
    surfaceVariant = WarmGray30,
    onSurfaceVariant = WarmGray80,

    // Outline
    outline = WarmGray60,
    outlineVariant = WarmGray30,

    // Inverse
    inverseSurface = WarmGray90,
    inverseOnSurface = WarmGray20,
    inversePrimary = Pink40,

    // Error
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    // Surface tint
    surfaceTint = Pink80
)

@Composable
fun MyLittleRoomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MyLittleRoomTypography,
        content = content
    )
}
