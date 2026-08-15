package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.domain.FeedRules
import com.xiaojiaoyin.baby.domain.WhoGrowthData
import com.xiaojiaoyin.baby.ui.components.LineChart
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.SegmentedField
import com.xiaojiaoyin.baby.ui.theme.Blue
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId

@Composable
fun GrowthChartScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val whoData = remember { WhoGrowthData(context) }
    var metric by remember { mutableStateOf(0) } // 0 身高 1 体重
    var currentBabyId by remember { mutableStateOf<Long?>(null) }
    var baby by remember { mutableStateOf<com.xiaojiaoyin.baby.data.db.entity.BabyEntity?>(null) }
    LaunchedEffect(Unit) {
        currentBabyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
        currentBabyId?.let { id ->
            baby = AppGraph.babyRepository.getById(id)
        }
    }
    val babyId = currentBabyId
    val records by remember(babyId) {
        if (babyId == null) flowOf(emptyList<RecordEntity>())
        else AppGraph.recordRepository.observeByType(babyId, RecordType.GROWTH)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val isMale = baby?.gender == "男"
    val points = records.mapNotNull { r ->
        val detail = runCatching { JSONObject(r.detailJson) }.getOrNull() ?: return@mapNotNull null
        val month = FeedRules.ageMonths(baby?.birthDateTime ?: return@mapNotNull null, r.occurredAt)
        val value = if (metric == 0) detail.optDouble("heightCm", 0.0)
        else detail.optDouble("weightKg", 0.0)
        if (value > 0) month.toFloat() to value.toFloat() else null
    }.sortedBy { it.first }

    val maxMonth = maxOf(points.lastOrNull()?.first?.toInt() ?: 12, 12).coerceAtMost(60)
    val refMonths = ((0..maxMonth step 3).toList() + points.map { it.first.toInt() }).distinct().sorted()

    fun percentilePoint(getter: (Int) -> com.xiaojiaoyin.baby.domain.WhoPoint?): List<Pair<Float, Float>> =
        refMonths.mapNotNull { m -> getter(m)?.let { m.toFloat() to it.p50.toFloat() } }

    val p3 = if (metric == 0)
        refMonths.mapNotNull { m -> whoData.heightPercentiles(isMale, m)?.let { m.toFloat() to it.p3.toFloat() } }
    else
        refMonths.mapNotNull { m -> whoData.weightPercentiles(isMale, m)?.let { m.toFloat() to it.p3.toFloat() } }
    val p50 = if (metric == 0)
        refMonths.mapNotNull { m -> whoData.heightPercentiles(isMale, m)?.let { m.toFloat() to it.p50.toFloat() } }
    else
        refMonths.mapNotNull { m -> whoData.weightPercentiles(isMale, m)?.let { m.toFloat() to it.p50.toFloat() } }
    val p97 = if (metric == 0)
        refMonths.mapNotNull { m -> whoData.heightPercentiles(isMale, m)?.let { m.toFloat() to it.p97.toFloat() } }
    else
        refMonths.mapNotNull { m -> whoData.weightPercentiles(isMale, m)?.let { m.toFloat() to it.p97.toFloat() } }

    val allValues = (p3 + p50 + p97).map { it.second } + points.map { it.second }
    val yMin = (allValues.minOrNull() ?: 0f) * 0.95f
    val yMax = (allValues.maxOrNull() ?: 1f) * 1.05f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("生长曲线", onBack)
        SegmentedField(
            label = "指标",
            options = listOf("身高", "体重"),
            selected = metric,
            onSelect = { metric = it }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Card, RoundedCornerShape(20.dp))
                .padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                LegendDot(Mint, "P50")
                LegendDot(TextSecondary.copy(alpha = 0.7f), "P3 / P97")
                LegendDot(Gold, "宝宝")
            }
            LineChart(
                refSeries = listOf(
                    TextSecondary.copy(alpha = 0.7f) to p3,
                    Mint to p50,
                    TextSecondary.copy(alpha = 0.7f) to p97
                ),
                userSeries = points,
                xMax = maxMonth.toFloat(),
                yMin = yMin,
                yMax = yMax,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("0 月", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                Text("${maxMonth} 月", fontSize = 10.sp, color = TextSecondary)
            }
        }

        Text(
            text = "历史记录",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        if (records.isEmpty()) {
            Text(
                "还没有生长记录，先去首页记一条吧",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            records.forEach { r ->
                val detail = runCatching { JSONObject(r.detailJson) }.getOrNull()
                val h = detail?.optDouble("heightCm", 0.0) ?: 0.0
                val w = detail?.optDouble("weightKg", 0.0) ?: 0.0
                val bmi = detail?.optDouble("bmi", 0.0) ?: 0.0
                val ev = detail?.optString("evaluation", "") ?: ""
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(Card, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(r.occurredAt),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (metric == 0) "${h}cm" else "${w}kg",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "BMI $bmi · $ev",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        modifier = Modifier.padding(end = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .padding(end = 4.dp)
                .background(color, RoundedCornerShape(4.dp))
                .padding(4.dp)
        )
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

private fun formatDate(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.monthValue}月${t.dayOfMonth}日"
}
