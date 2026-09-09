package com.xiaojiaoyin.baby.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.PhotoStorage
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.data.db.entity.tagsList
import com.xiaojiaoyin.baby.data.db.entity.withTags
import com.xiaojiaoyin.baby.ui.common.rememberProUnlocked
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
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
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun AlbumScreen(onUpgrade: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val babyId by com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId()
    val photos by remember(babyId) {
        if (babyId == null) flowOf(emptyList<RecordEntity>())
        else AppGraph.recordRepository.observeByType(babyId!!, RecordType.PHOTO)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    var query by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var previewId by remember { mutableStateOf<Long?>(null) }
    var showUpgrade by remember { mutableStateOf(false) }
    val unlocked by rememberProUnlocked()
    val FREE_PHOTO_LIMIT = 30

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                if (!unlocked && photos.size >= FREE_PHOTO_LIMIT) {
                    showUpgrade = true
                } else {
                    val path = PhotoStorage.saveImage(context, uri)
                    if (path != null && babyId != null) {
                        AppGraph.recordRepository.add(
                            babyId = babyId!!,
                            type = RecordType.PHOTO,
                            occurredAt = System.currentTimeMillis(),
                            detailJson = JSONObject().put("path", path).toString()
                        )
                    }
                }
            }
        }
    }

    val months = photos.map { monthKey(it.occurredAt) }.distinct().sortedDescending()
    val tags = photos.flatMap { it.tagsList() }.distinct().sorted()

    val q = query.trim()
    val filtered = photos.filter { p ->
        (q.isBlank() ||
            p.note.contains(q, ignoreCase = true) ||
            p.tagsList().any { it.contains(q, ignoreCase = true) }) &&
            (selectedMonth == null || monthKey(p.occurredAt) == selectedMonth) &&
            (selectedTag == null || p.tagsList().contains(selectedTag))
    }
    val showHeaders = selectedMonth == null
    val grouped = if (showHeaders) filtered.groupBy { monthLabel(it.occurredAt) } else emptyMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            text = "相册",
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
                placeholder = { Text("搜索备注或标签…", fontSize = 13.sp, color = TextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Card,
                    unfocusedContainerColor = Card,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "＋ 添加",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Mint,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable { picker.launch(arrayOf("image/*")) }
            )
        }
        FilterChipRow(
            labels = months.map { monthLabelOf(it) },
            selected = selectedMonth?.let { monthLabelOf(it) },
            onSelect = { label ->
                selectedMonth = if (label == null) null else months.firstOrNull { monthLabelOf(it) == label }
            },
            modifier = Modifier.padding(top = 10.dp)
        )
        if (tags.isNotEmpty()) {
            FilterChipRow(
                labels = tags,
                selected = selectedTag,
                onSelect = { selectedTag = it },
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (filtered.isEmpty()) {
            Text(
                text = if (photos.isEmpty()) "还没有照片，点右上角添加" else "没有匹配的照片，试试清除筛选",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 70.dp)
            ) {
                if (showHeaders) {
                    grouped.forEach { (month, list) ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = month,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(list, key = { it.id }) { record ->
                            PhotoGridItem(context = context, record = record) { previewId = record.id }
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { record ->
                        PhotoGridItem(context = context, record = record) { previewId = record.id }
                    }
                }
            }
        }
    }

    val previewRecord = photos.firstOrNull { it.id == previewId }
    if (previewRecord != null) {
        PhotoPreviewDialog(
            context = context,
            record = previewRecord,
            onDismiss = { previewId = null },
            onDelete = {
                scope.launch {
                    PhotoStorage.delete(context, previewRecord.detailJsonPath())
                    AppGraph.recordRepository.delete(previewRecord)
                }
                previewId = null
            },
            onSave = { note, tagList ->
                scope.launch {
                    AppGraph.recordRepository.updateWithTimestamp(previewRecord.copy(note = note.trim()).withTags(tagList))
                }
                previewId = null
            }
        )
    }

    if (showUpgrade) {
        AlertDialog(
            onDismissRequest = { showUpgrade = false },
            title = { Text("照片空间", fontWeight = FontWeight.Bold) },
            text = { Text("免费版最多存 $FREE_PHOTO_LIMIT 张照片，升级 Pro 无限照片。") },
            confirmButton = {
                TextButton(onClick = {
                    showUpgrade = false
                    onUpgrade()
                }) { Text("去升级", color = Mint) }
            },
            dismissButton = {
                TextButton(onClick = { showUpgrade = false }) { Text("暂不") }
            }
        )
    }
}

@Composable
private fun FilterChipRow(
    labels: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        item { Chip(label = "全部", active = selected == null, onClick = { onSelect(null) }) }
        lazyItems(labels) { label ->
            Chip(label = label, active = selected == label, onClick = { onSelect(label) })
        }
    }
}

@Composable
private fun Chip(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (active) Mint else Card, RoundedCornerShape(50.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) Color.White else TextSecondary
        )
    }
}

@Composable
private fun PhotoGridItem(
    context: android.content.Context,
    record: RecordEntity,
    onClick: () -> Unit
) {
    val path = record.detailJsonPath()
    // IO 线程采样解码缩略图，避免主线程全尺寸解码卡顿/OOM
    val bitmap by produceState<Bitmap?>(null, record.id, path) {
        value = if (path.isBlank()) null
        else withContext(Dispatchers.IO) {
            PhotoStorage.decodeThumb(PhotoStorage.loadFile(context, path))
        }
    }
    Box(
        modifier = Modifier
            .padding(3.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(PhotoPlaceholderBg)
            .clickable(onClick = onClick)
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = record.note,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        if (record.note.isNotBlank()) {
            Text(
                text = record.note,
                fontSize = 10.sp,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun PhotoPreviewDialog(
    context: android.content.Context,
    record: RecordEntity,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: (String, List<String>) -> Unit
) {
    var note by remember(record.id) { mutableStateOf(record.note) }
    var tagList by remember(record.id) { mutableStateOf(record.tagsList()) }
    var newTag by remember(record.id) { mutableStateOf("") }
    val path = record.detailJsonPath()
    // 预览图保留全尺寸，但移出组合阶段到 IO 线程解码
    val bitmap by produceState<Bitmap?>(null, record.id, path) {
        value = if (path.isBlank()) null
        else withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(PhotoStorage.loadFile(context, path).absolutePath)
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("照片", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
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
                if (tagList.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 10.dp)
                    ) {
                        tagList.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(MintLight, RoundedCornerShape(50.dp))
                                    .padding(start = 10.dp, end = 4.dp, top = 3.dp, bottom = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(tag, fontSize = 12.sp, color = Mint, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = " ×",
                                        fontSize = 13.sp,
                                        color = Mint,
                                        modifier = Modifier
                                            .padding(start = 3.dp, end = 6.dp)
                                            .clickable { tagList = tagList - tag }
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    TextField(
                        value = newTag,
                        onValueChange = { newTag = it.replace(",", "") },
                        placeholder = { Text("添加标签，如 满月", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "添加",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Mint,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable {
                                val t = newTag.trim()
                                if (t.isNotEmpty() && t !in tagList) tagList = tagList + t
                                newTag = ""
                            }
                    )
                }
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("备注，如 第一次笑出声", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
                Text(
                    text = "拍摄于 ${monthLabel(record.occurredAt)} ${timeLabel(record.occurredAt)}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(note, tagList) }) { Text("保存") }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    if (path.isNotBlank()) {
                        val file = PhotoStorage.loadFile(context, path)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(share, "分享照片"))
                    }
                }) { Text("分享", color = Mint) }
                TextButton(onClick = onDelete) { Text("删除", color = Red) }
            }
        }
    )
}

private fun RecordEntity.detailJsonPath(): String =
    runCatching { JSONObject(detailJson).optString("path", "") }.getOrDefault("")

private fun monthKey(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.year}-${t.monthValue}"
}

private fun monthLabelOf(key: String): String {
    val parts = key.split("-")
    return if (parts.size == 2) "${parts[0]}年${parts[1].toInt()}月" else key
}

private fun monthLabel(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.year}年${t.monthValue}月"
}

private fun timeLabel(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return String.format("%02d:%02d", t.hour, t.minute)
}
