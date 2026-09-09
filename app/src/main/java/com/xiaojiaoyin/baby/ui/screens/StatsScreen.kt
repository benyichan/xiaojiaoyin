package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import com.xiaojiaoyin.baby.domain.StatsCalculator
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.Pink
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun StatsScreen(onOpenChart: () -> Unit, onUpgrade: () -> Unit) {
    // 统计看板为基础功能，免费开放；WHO 生长曲线（onOpenChart 详情页）仍为 Pro
    val babyId by com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId()
    val records by remember(babyId) {
        if (babyId == null) flowOf(emptyList<RecordEntity>())
        else AppGraph.recordRepository.observeAll(babyId!!)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val todos by remember(babyId) {
        if (babyId == null) flowOf(emptyList<TodoEntity>())
        else AppGraph.todoRepository.observeAll(babyId!!)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val feedingByDay = lastNDaysFeeding(records, days = 30)
    val cryingReasons = cryingReasonDistribution(records)
    val overview = StatsCalculator.monthlyOverview(records, System.currentTimeMillis())
    val feedingRatio = StatsCalculator.feedingTypeRatio(records)
    val cryingBuckets = StatsCalculator.cryingTimeBuckets(records)
    val (todoDone, todoTotal) = StatsCalculator.todoCompletion(todos)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding().padding(bottom = 70.dp)
    ) {
        Text(
            text = "统计",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
        )

        StatCard(
            title = "生长曲线",
            desc = "身高 / 体重对照 WHO 标准",
            onClick = onOpenChart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendChip(color = Mint, label = "P50")
                LegendChip(color = TextSecondary, label = "P3/P97")
                LegendChip(color = Gold, label = "宝宝")
            }
        }

        StatCard(title = "喂养统计", desc = "近 30 天每日记录次数") {
            if (feedingByDay.maxOrNull() == 0) {
                Text("暂无喂养记录", fontSize = 12.sp, color = TextSecondary)
            } else {
                BarChart(values = feedingByDay, modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("-30 天", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                    Text("今天", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }

        StatCard(title = "月度概览", desc = "本月成长记录") {
            if (overview.total == 0) {
                Text("本月还没有记录", fontSize = 12.sp, color = TextSecondary)
            } else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OverviewCell("记录", overview.total.toString(), Modifier.weight(1f))
                    OverviewCell("喂养", overview.feeding.toString(), Modifier.weight(1f))
                    OverviewCell("哭闹", overview.crying.toString(), Modifier.weight(1f))
                    OverviewCell("生长", overview.growth.toString(), Modifier.weight(1f))
                    OverviewCell("节点", overview.nodes.toString(), Modifier.weight(1f))
                }
            }
        }

        StatCard(title = "喂养类型占比", desc = "母乳 / 奶粉 / 辅食") {
            val ratioTotal = feedingRatio.values.sum()
            if (ratioTotal == 0) {
                Text("暂无喂养记录", fontSize = 12.sp, color = TextSecondary)
            } else {
                feedingRatio.forEach { (kind, count) ->
                    if (count > 0) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Text(kind, fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                            Text("$count 次 · ${count * 100 / ratioTotal}%", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        StatCard(title = "哭闹时段", desc = "按 3 小时时段分布") {
            val bucketSum = cryingBuckets.sum()
            if (bucketSum == 0) {
                Text("暂无哭闹记录", fontSize = 12.sp, color = TextSecondary)
            } else {
                val maxBucket = cryingBuckets.maxOrNull() ?: 1
                cryingBuckets.forEachIndexed { i, count ->
                    if (count > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${i * 3}-${i * 3 + 2}点",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.width(44.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .background(Mint.copy(alpha = 0.15f), RoundedCornerShape(5.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(count.toFloat() / maxBucket.coerceAtLeast(1))
                                        .fillMaxHeight()
                                        .background(Mint, RoundedCornerShape(5.dp))
                                )
                            }
                            Text(
                                "$count",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .width(28.dp)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        StatCard(title = "待办完成率", desc = "已完成 / 全部待办") {
            if (todoTotal == 0) {
                Text("暂无待办", fontSize = 12.sp, color = TextSecondary)
            } else {
                Text(
                    "$todoDone / $todoTotal · ${todoDone * 100 / todoTotal}%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Mint
                )
            }
        }

        StatCard(title = "哭闹分布", desc = "按原因占比") {
            val total = cryingReasons.values.sum()
            if (total == 0) {
                Text("暂无哭闹记录", fontSize = 12.sp, color = TextSecondary)
            } else {
                cryingReasons.forEach { (reason, count) ->
                    val ratio = count.toFloat() / total
                    Column(modifier = Modifier.padding(vertical = 5.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(reason, fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                            Text("$count 次 · ${(ratio * 100).toInt()}%", fontSize = 11.sp, color = TextSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio.coerceAtLeast(0.02f))
                                .padding(top = 3.dp)
                                .background(Pink.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .height(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun StatCard(
    title: String,
    desc: String,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(Card, RoundedCornerShape(18.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
            if (onClick != null) {
                Text("查看 ›", fontSize = 12.sp, color = Mint)
            }
        }
        Text(desc, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 3.dp, bottom = 8.dp))
        content()
    }
}

@Composable
private fun LegendChip(color: Color, label: String) {
    Row(
        modifier = Modifier.padding(end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(end = 4.dp)
                .background(color, RoundedCornerShape(4.dp))
                .padding(4.dp)
        )
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun BarChart(values: List<Int>, modifier: Modifier = Modifier) {
    val max = (values.maxOrNull() ?: 1).coerceAtLeast(1)
    Canvas(modifier = modifier) {
        val slot = size.width / values.size
        val barWidth = slot * 0.6f
        values.forEachIndexed { i, v ->
            val h = size.height * v / max
            drawRoundRect(
                color = Mint,
                topLeft = Offset(i * slot + (slot - barWidth) / 2, size.height - h),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }
    }
}

private fun lastNDaysFeeding(records: List<RecordEntity>, days: Int): List<Int> {
    val zone = ZoneId.of("Asia/Shanghai")
    val today = Instant.now().atZone(zone).toLocalDate()
    val feeding = records.filter { it.type == RecordType.FEEDING }
    return (days - 1 downTo 0).map { offset ->
        val day = today.minus(offset.toLong(), ChronoUnit.DAYS)
        feeding.count {
            Instant.ofEpochMilli(it.occurredAt).atZone(zone).toLocalDate() == day
        }
    }
}

private fun cryingReasonDistribution(records: List<RecordEntity>): Map<String, Int> {
    val result = linkedMapOf("饿了" to 0, "困了" to 0, "不舒服" to 0, "其他" to 0)
    records.filter { it.type == RecordType.CRYING }.forEach { r ->
        val reason = runCatching { JSONObject(r.detailJson).optString("reason", "其他") }.getOrDefault("其他")
        result[reason] = (result[reason] ?: 0) + 1
    }
    return result
}
