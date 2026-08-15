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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.GoldLight
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId

@Composable
fun GrowthScreen(onAddNode: () -> Unit) {
    val scope = rememberCoroutineScope()
    val babyId by com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId()
    val nodes by remember(babyId) {
        if (babyId == null) flowOf(emptyList<RecordEntity>())
        else AppGraph.recordRepository.observeByType(babyId!!, RecordType.NODE)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    var query by remember { mutableStateOf("") }
    var previewId by remember { mutableStateOf<Long?>(null) }

    val filtered = if (query.isBlank()) nodes
    else nodes.filter {
        it.title().contains(query.trim(), ignoreCase = true) ||
            it.note.contains(query.trim(), ignoreCase = true)
    }
    val grouped = filtered.groupBy { monthLabel(it.occurredAt) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            text = "成长时间轴",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("搜索节点…", fontSize = 13.sp, color = TextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Card,
                    unfocusedContainerColor = Card,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = "＋ 记录节点",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Mint,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable(onClick = onAddNode)
            )
        }

        if (grouped.isEmpty()) {
            Text(
                text = if (nodes.isEmpty()) "还没有重要节点，记下第一次翻身、第一次笑吧" else "没有匹配的节点",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 70.dp)
            ) {
                grouped.forEach { (month, list) ->
                    item(key = "h-$month") {
                        Text(
                            text = month,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                    list.forEach { node ->
                        item(key = node.id) {
                            NodeTimelineItem(node = node) { previewId = node.id }
                        }
                    }
                }
            }
        }
    }

    val previewNode = nodes.firstOrNull { it.id == previewId }
    if (previewNode != null) {
        AlertDialog(
            onDismissRequest = { previewId = null },
            title = { Text(previewNode.title(), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "${monthLabel(previewNode.occurredAt)} ${timeLabel(previewNode.occurredAt)}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    if (previewNode.note.isNotBlank()) {
                        Text(
                            text = previewNode.note,
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
                    scope.launch { AppGraph.recordRepository.delete(previewNode) }
                    previewId = null
                }) { Text("删除", color = Color(0xFFD96A6A)) }
            }
        )
    }
}

@Composable
private fun NodeTimelineItem(node: RecordEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 14.dp)
                .background(Gold, CircleShape)
                .padding(5.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, bottom = 8.dp)
                .background(Card, RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding(14.dp)
        ) {
            Text(node.title(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
                text = "${monthLabel(node.occurredAt)} ${timeLabel(node.occurredAt)}",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (node.note.isNotBlank()) {
                Text(
                    text = node.note,
                    fontSize = 12.sp,
                    color = TextPrimary.copy(alpha = 0.75f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Text(
                text = "⭐ 重要",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Gold,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(GoldLight, RoundedCornerShape(9.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

internal fun RecordEntity.title(): String =
    runCatching { JSONObject(detailJson).optString("title", "重要节点") }
        .getOrDefault("重要节点")

private fun monthLabel(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.year}年${t.monthValue}月"
}

private fun timeLabel(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.monthValue}月${t.dayOfMonth}日 ${String.format("%02d:%02d", t.hour, t.minute)}"
}
