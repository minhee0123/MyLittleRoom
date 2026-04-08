package com.mylittleroom.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** 라이트 모드 컬러 스킴 — Muted Rose 기반의 밝은 테마 */
private val LightColorScheme = lightColorScheme(
    // Primary: Muted Rose
    primary = Rose40,
    onPrimary = Color.White,
    primaryContainer = Rose90,
    onPrimaryContainer = Rose20,

    // Secondary: Dusty Blue
    secondary = DustyBlue40,
    onSecondary = Color.White,
    secondaryContainer = DustyBlue90,
    onSecondaryContainer = DustyBlue20,

    // Tertiary: Sage
    tertiary = Sage40,
    onTertiary = Color.White,
    tertiaryContainer = Sage90,
    onTertiaryContainer = Sage20,

    // Background & Surface: 깔끔한 화이트
    background = Color.White,
    onBackground = Gray10,
    surface = Color.White,
    onSurface = Gray10,
    surfaceVariant = Gray95,
    onSurfaceVariant = Gray40,

    // Outline
    outline = Gray60,
    outlineVariant = Gray90,

    // Inverse
    inverseSurface = Gray20,
    inverseOnSurface = Gray95,
    inversePrimary = Rose70,

    // Error
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,

    // Surface tint
    surfaceTint = Rose40
)

/** 다크 모드 컬러 스킴 — Muted Rose 기반의 어두운 테마 */
private val DarkColorScheme = darkColorScheme(
    // Primary: Muted Rose
    primary = Rose70,
    onPrimary = Rose20,
    primaryContainer = Rose30,
    onPrimaryContainer = Rose90,

    // Secondary: Dusty Blue
    secondary = DustyBlue70,
    onSecondary = DustyBlue20,
    secondaryContainer = DustyBlue30,
    onSecondaryContainer = DustyBlue90,

    // Tertiary: Sage
    tertiary = Sage70,
    onTertiary = Sage20,
    tertiaryContainer = Sage30,
    onTertiaryContainer = Sage90,

    // Background & Surface
    background = Gray10,
    onBackground = Gray90,
    surface = Gray10,
    onSurface = Gray90,
    surfaceVariant = Gray30,
    onSurfaceVariant = Gray80,

    // Outline
    outline = Gray60,
    outlineVariant = Gray30,

    // Inverse
    inverseSurface = Gray90,
    inverseOnSurface = Gray20,
    inversePrimary = Rose40,

    // Error
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    // Surface tint
    surfaceTint = Rose70
)

/** 앱 전역 테마 — 시스템 다크 모드에 따라 Light/Dark 스킴을 자동 전환 */
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
