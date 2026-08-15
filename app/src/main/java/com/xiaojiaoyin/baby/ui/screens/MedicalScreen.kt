package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Blue
import com.xiaojiaoyin.baby.ui.theme.BlueLight
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.GoldLight
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.Pink
import com.xiaojiaoyin.baby.ui.theme.PinkLight
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId

private val MEDICAL_CATEGORIES = listOf("疫苗", "体检", "就诊", "用药")

@Composable
fun MedicalScreen(onBack: () -> Unit, onAdd: () -> Unit) {
    val scope = rememberCoroutineScope()
    var currentBabyId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(Unit) {
        currentBabyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
    }
    val babyId = currentBabyId
    val records by remember(babyId) {
        if (babyId == null) flowOf(emptyList<RecordEntity>())
        else AppGraph.recordRepository.observeByType(babyId, RecordType.MEDICAL)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    var filter by remember { mutableStateOf("全部") }
    var previewId by remember { mutableStateOf<Long?>(null) }

    val filtered = if (filter == "全部") records
    else records.filter { it.category() == filter }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        OverlayHeader("医疗记录", onBack)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChips(
                options = listOf("全部") + MEDICAL_CATEGORIES,
                selected = filter,
                onSelect = { filter = it },
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "＋ 记录",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Mint,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable(onClick = onAdd)
            )
        }

        if (filtered.isEmpty()) {
            Text(
                text = "还没有医疗记录，点右上角记录疫苗、体检、就诊或用药",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 30.dp)
            ) {
                items(filtered.size) { index ->
                    val r = filtered[index]
                    MedicalRow(record = r) { previewId = r.id }
                }
            }
        }
    }

    val preview = records.firstOrNull { it.id == previewId }
    if (preview != null) {
        AlertDialog(
            onDismissRequest = { previewId = null },
            title = { Text(preview.medicalTitle(), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("分类：${preview.category()}", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        formatDateTime(preview.occurredAt),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (preview.medicalDetail().isNotBlank()) {
                        Text(
                            preview.medicalDetail(),
                            fontSize = 13.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { previewId = null }) { Text("关闭") }
            },
            dismissButton = {
                TextButton(onClick = {
                    scope.launch { AppGraph.recordRepository.delete(preview) }
                    previewId = null
                }) { Text("删除", color = Color(0xFFD96A6A)) }
            }
        )
    }
}

@Composable
private fun FilterChips(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        options.forEach { option ->
            val active = option == selected
            Text(
                text = option,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (active) Color.White else TextSecondary,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .background(
                        if (active) Mint else Card,
                        RoundedCornerShape(99.dp)
                    )
                    .clickable { onSelect(option) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun MedicalRow(record: RecordEntity, onClick: () -> Unit) {
    val category = record.category()
    val (bg, fg) = when (category) {
        "疫苗" -> MintLight to Mint
        "体检" -> BlueLight to Blue
        "就诊" -> GoldLight to Gold
        else -> PinkLight to Pink
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .background(Card, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            modifier = Modifier
                .background(bg, RoundedCornerShape(9.dp))
                .padding(horizontal = 9.dp, vertical = 5.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(record.medicalTitle(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
                formatDateTime(record.occurredAt),
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

private fun RecordEntity.category(): String =
    runCatching { JSONObject(detailJson).optString("category", "其他") }.getOrDefault("其他")

private fun RecordEntity.medicalTitle(): String =
    runCatching { JSONObject(detailJson).optString("title", "医疗记录") }.getOrDefault("医疗记录")

private fun RecordEntity.medicalDetail(): String =
    runCatching { JSONObject(detailJson).optString("detail", "") }.getOrDefault("")

private fun formatDateTime(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.year}-${t.monthValue.toString().padStart(2, '0')}-${t.dayOfMonth.toString().padStart(2, '0')}"
}
