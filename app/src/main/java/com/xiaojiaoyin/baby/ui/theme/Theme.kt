package com.xiaojiaoyin.baby.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.DelegatableNode

/** 无涟漪指示器：对齐收纳管家零涟漪风格，全 app clickable 不再出现灰色色块 */
private class NoIndicationNode : Modifier.Node()

private object NoIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = NoIndicationNode()
    override fun equals(other: Any?) = other === this
    override fun hashCode() = java.lang.System.identityHashCode(this)
}

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
    CompositionLocalProvider(LocalIndication provides NoIndication) {
        MaterialTheme(
            colorScheme = BabyColorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
