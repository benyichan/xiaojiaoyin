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
import com.xiaojiaoyin.baby.ui.components.TagKind
import com.xiaojiaoyin.baby.ui.components.TypeTag
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId

@Composable
fun FeedingZoneScreen(onBack: () -> Unit, onAdd: () -> Unit) {
    RecordZoneScreen(
        title = "喂养专区",
        type = RecordType.FEEDING,
        emptyText = "还没有喂养记录，点右上角记录",
        onBack = onBack,
        onAdd = onAdd
    ) { r ->
        val detail = runCatching { JSONObject(r.detailJson) }.getOrNull()
        val kind = detail?.optString("kind", "母乳") ?: "母乳"
        val amount = detail?.optString("amount", "") ?: ""
        ZoneLine(
            tag = { TypeTag(TagKind.FEEDING, "喂养") },
            title = "$kind · $amount",
            sub = formatZoneTime(r.occurredAt),
            note = r.note
        )
    }
}

@Composable
fun CryingZoneScreen(onBack: () -> Unit, onAdd: () -> Unit) {
    RecordZoneScreen(
        title = "哭闹专区",
        type = RecordType.CRYING,
        emptyText = "还没有哭闹记录，点右上角记录",
        onBack = onBack,
        onAdd = onAdd
    ) { r ->
        val detail = runCatching { JSONObject(r.detailJson) }.getOrNull()
        val reason = detail?.optString("reason", "哭闹") ?: "哭闹"
        val comfort = detail?.optString("comfort", "") ?: ""
        ZoneLine(
            tag = { TypeTag(TagKind.CRYING, "哭闹") },
            title = reason,
            sub = formatZoneTime(r.occurredAt),
            note = comfort.ifBlank { r.note }
        )
    }
}

@Composable
private fun RecordZoneScreen(
    title: String,
    type: RecordType,
    emptyText: String,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    line: @Composable (RecordEntity) -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentBabyId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(Unit) {
        currentBabyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
    }
    val babyId = currentBabyId
    val records by remember(babyId) {
        if (babyId == null) flowOf(emptyList<RecordEntity>())
        else AppGraph.recordRepository.observeByType(babyId, type)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    var previewId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        OverlayHeader(title, onBack)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(Mint, RoundedCornerShape(14.dp))
                .clickableZone(onAdd)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("＋ 记录", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (records.isEmpty()) {
            Text(
                text = emptyText,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 30.dp)
            ) {
                items(records.size) { index ->
                    val r = records[index]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .background(Card, RoundedCornerShape(18.dp))
                            .clickableZone { previewId = r.id }
                            .padding(13.dp)
                    ) {
                        line(r)
                    }
                }
            }
        }
    }

    val preview = records.firstOrNull { it.id == previewId }
    if (preview != null) {
        AlertDialog(
            onDismissRequest = { previewId = null },
            title = { Text("记录详情", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    line(preview)
                    if (preview.note.isNotBlank()) {
                        Text(
                            preview.note,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 6.dp)
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
private fun ZoneLine(
    tag: @Composable () -> Unit,
    title: String,
    sub: String,
    note: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        tag()
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(sub, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
            if (note.isNotBlank()) {
                Text(note, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

private fun Modifier.clickableZone(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)

private fun formatZoneTime(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.monthValue}月${t.dayOfMonth}日 ${String.format("%02d:%02d", t.hour, t.minute)}"
}
