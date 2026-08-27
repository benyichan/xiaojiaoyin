package com.xiaojiaoyin.baby.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.PhotoStorage
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.GoldLight
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import com.xiaojiaoyin.baby.ui.theme.PhotoPlaceholderBg
import com.xiaojiaoyin.baby.ui.theme.Red

@Composable
fun GrowthScreen(onAddNode: () -> Unit, onEditNode: (Long) -> Unit) {
    val scope = rememberCoroutineScope()
    val babyId by com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId()
    val nodes by remember(babyId) {
        if (babyId == null) flowOf(emptyList<RecordEntity>())
        else AppGraph.recordRepository.observeByType(babyId!!, RecordType.NODE)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    var query by remember { mutableStateOf("") }
    var previewId by remember { mutableStateOf<Long?>(null) }
    var fullPhotoPath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

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
                text = if (nodes.isEmpty()) "还没有成长节点，记下第一次翻身、第一次笑吧" else "没有匹配的节点",
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
    val previewPhotos by remember(previewNode?.id) {
        if (previewNode == null) flowOf(emptyList<RecordEntity>())
        else AppGraph.recordRepository.observeByParent(previewNode.id)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    if (previewNode != null) {
        AlertDialog(
            onDismissRequest = { previewId = null },
            title = { Text(previewNode.title(), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = detailTimeLabel(previewNode.occurredAt),
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
                    if (previewPhotos.isNotEmpty()) {
                        Text(
                            text = "配图 ${previewPhotos.size} 张",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            previewPhotos.forEach { photo ->
                                NodePhotoThumb(
                                    context = context,
                                    record = photo,
                                    onClick = { fullPhotoPath = photo.detailJsonPath() }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { previewId = null }) { Text("关闭") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        onEditNode(previewNode.id)
                        previewId = null
                    }) { Text("编辑", color = Mint) }
                    TextButton(onClick = {
                        scope.launch {
                            AppGraph.recordRepository.getByParent(previewNode.id).forEach { p ->
                                PhotoStorage.delete(context, p.detailJsonPath())
                                AppGraph.recordRepository.delete(p)
                            }
                            AppGraph.recordRepository.delete(previewNode)
                        }
                        previewId = null
                    }) { Text("删除", color = Red) }
                }
            }
        )
    }

    val fullPhoto = previewPhotos.firstOrNull { it.detailJsonPath() == fullPhotoPath }
    if (fullPhoto != null) {
        val bitmap by produceState<Bitmap?>(null, fullPhoto.id) {
            val p = fullPhoto.detailJsonPath()
            value = if (p.isBlank()) null
            else withContext(Dispatchers.IO) {
                BitmapFactory.decodeFile(PhotoStorage.loadFile(context, p).absolutePath)
            }
        }
        AlertDialog(
            onDismissRequest = { fullPhotoPath = null },
            confirmButton = {
                TextButton(onClick = { fullPhotoPath = null }) { Text("关闭") }
            },
            text = {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
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
                .height(84.dp)
                .padding(14.dp)
        ) {
            Text(
                node.title(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = timeLabel(node.occurredAt),
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

internal fun RecordEntity.title(): String =
    runCatching { JSONObject(detailJson).optString("title", "重要节点") }
        .getOrDefault("重要节点")

private fun RecordEntity.detailJsonPath(): String =
    runCatching { JSONObject(detailJson).optString("path", "") }.getOrDefault("")

@Composable
private fun NodePhotoThumb(
    context: android.content.Context,
    record: RecordEntity,
    onClick: () -> Unit
) {
    val path = record.detailJsonPath()
    val bitmap by produceState<Bitmap?>(null, record.id, path) {
        value = if (path.isBlank()) null
        else withContext(Dispatchers.IO) {
            PhotoStorage.decodeThumb(PhotoStorage.loadFile(context, path))
        }
    }
    Box(
        modifier = Modifier
            .size(56.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(PhotoPlaceholderBg)
            .clickable(onClick = onClick)
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private fun monthLabel(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.year}年${t.monthValue}月"
}

private fun timeLabel(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.monthValue}月${t.dayOfMonth}日 ${String.format("%02d:%02d", t.hour, t.minute)}"
}

private fun detailTimeLabel(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.year}年${t.monthValue}月${t.dayOfMonth}日 ${String.format("%02d:%02d", t.hour, t.minute)}"
}
