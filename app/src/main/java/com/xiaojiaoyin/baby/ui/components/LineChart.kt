package com.xiaojiaoyin.baby.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextTertiary

/**
 * 简单折线图：参考线（P3/P50/P97）+ 用户数据点。
 * 不画坐标文字，标签由外部布局提供。
 */
@Composable
fun LineChart(
    refSeries: List<Pair<Color, List<Pair<Float, Float>>>>,
    userSeries: List<Pair<Float, Float>>,
    xMax: Float,
    yMin: Float,
    yMax: Float,
    modifier: Modifier = Modifier
) {
    val pad = 20f
    Canvas(modifier = modifier) {
        if (yMax <= yMin) return@Canvas
        val w = size.width - pad * 2
        val h = size.height - pad * 2
        fun x(v: Float) = pad + v / xMax * w
        fun y(v: Float) = pad + (yMax - v) / (yMax - yMin) * h

        // 背景网格（4 条水平线）
        for (i in 0..4) {
            val gy = pad + h * i / 4f
            drawLine(
                color = TextTertiary.copy(alpha = 0.25f),
                start = Offset(pad, gy),
                end = Offset(size.width - pad, gy),
                strokeWidth = 1f
            )
        }

        // 参考线
        refSeries.forEach { (color, points) ->
            if (points.size >= 2) {
                val path = Path()
                points.forEachIndexed { i, (mx, my) ->
                    if (i == 0) path.moveTo(x(mx), y(my)) else path.lineTo(x(mx), y(my))
                }
                drawPath(
                    path = path,
                    color = color.copy(alpha = 0.7f),
                    style = Stroke(width = 1.5f)
                )
            }
        }

        // 用户折线
        if (userSeries.size >= 2) {
            val path = Path()
            userSeries.forEachIndexed { i, (mx, my) ->
                if (i == 0) path.moveTo(x(mx), y(my)) else path.lineTo(x(mx), y(my))
            }
            drawPath(
                path = path,
                color = Mint,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
        }
        // 用户数据点
        userSeries.forEach { (mx, my) ->
            drawCircle(
                color = Gold,
                radius = 4f,
                center = Offset(x(mx), y(my))
            )
        }
    }
}
