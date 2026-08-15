package com.xiaojiaoyin.baby.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BabyColorScheme = lightColorScheme(
    primary = Mint,
    onPrimary = Card,
    primaryContainer = MintLight,
    onPrimaryContainer = MintDeep,
    secondary = Gold,
    background = Bg,
    onBackground = TextPrimary,
    surface = Card,
    onSurface = TextPrimary,
    surfaceVariant = MintLight,
    onSurfaceVariant = TextSecondary
)

@Composable
fun BabyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BabyColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
