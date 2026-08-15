package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.TagKind
import com.xiaojiaoyin.baby.ui.components.TypeTag
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId

@Composable
fun AnnivScreen(onBack: () -> Unit) {
    var currentBabyId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(Unit) {
        currentBabyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
    }
    val babyId = currentBabyId
    val records by remember(babyId) {
        if (babyId == null) flowOf(emptyList<RecordEntity>())
        else AppGraph.recordRepository.observeAll(babyId)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val zone = ZoneId.of("Asia/Shanghai")
    val today = Instant.now().atZone(zone)
    val pastOnThisDay = records
        .filter {
            val t = Instant.ofEpochMilli(it.occurredAt).atZone(zone)
            t.monthValue == today.monthValue && t.dayOfMonth == today.dayOfMonth && t.year < today.year
        }
        .sortedByDescending { it.occurredAt }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        OverlayHeader("那年今日", onBack)
        if (pastOnThisDay.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Card, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "今天是 ${today.monthValue}月${today.dayOfMonth}日",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "记录满一年后，这里会展示往年今天发生的事——\n第一次翻身、第一次笑、第一次生病……",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 30.dp)
            ) {
                items(pastOnThisDay.size) { index ->
                    val r = pastOnThisDay[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .background(Card, RoundedCornerShape(18.dp))
                            .padding(13.dp)
                    ) {
                        TypeTag(kindOf(r.type), labelOf(r.type))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(recordTitle(r), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                "${Instant.ofEpochMilli(r.occurredAt).atZone(zone).year}年",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun kindOf(type: com.xiaojiaoyin.baby.data.db.entity.RecordType): TagKind = when (type) {
    com.xiaojiaoyin.baby.data.db.entity.RecordType.FEEDING -> TagKind.FEEDING
    com.xiaojiaoyin.baby.data.db.entity.RecordType.CRYING -> TagKind.CRYING
    com.xiaojiaoyin.baby.data.db.entity.RecordType.GROWTH -> TagKind.GROWTH
    com.xiaojiaoyin.baby.data.db.entity.RecordType.PHOTO -> TagKind.PHOTO
    else -> TagKind.NODE
}

private fun labelOf(type: com.xiaojiaoyin.baby.data.db.entity.RecordType): String = when (type) {
    com.xiaojiaoyin.baby.data.db.entity.RecordType.FEEDING -> "喂养"
    com.xiaojiaoyin.baby.data.db.entity.RecordType.CRYING -> "哭闹"
    com.xiaojiaoyin.baby.data.db.entity.RecordType.GROWTH -> "生长"
    com.xiaojiaoyin.baby.data.db.entity.RecordType.PHOTO -> "相册"
    com.xiaojiaoyin.baby.data.db.entity.RecordType.NODE -> "节点"
    com.xiaojiaoyin.baby.data.db.entity.RecordType.MEDICAL -> "医疗"
}

private fun recordTitle(r: RecordEntity): String = when (r.type) {
    com.xiaojiaoyin.baby.data.db.entity.RecordType.NODE ->
        runCatching { JSONObject(r.detailJson).optString("title", "重要节点") }.getOrDefault("重要节点")
    com.xiaojiaoyin.baby.data.db.entity.RecordType.PHOTO -> r.note.ifBlank { "照片" }
    com.xiaojiaoyin.baby.data.db.entity.RecordType.MEDICAL ->
        runCatching { JSONObject(r.detailJson).optString("title", "医疗记录") }.getOrDefault("医疗记录")
    com.xiaojiaoyin.baby.data.db.entity.RecordType.FEEDING -> {
        val d = runCatching { JSONObject(r.detailJson) }.getOrNull()
        "喂养 · ${d?.optString("amount", "")}"
    }
    else -> "记录"
}
