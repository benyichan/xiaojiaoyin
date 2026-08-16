package com.xiaojiaoyin.baby.ui.screens

import android.graphics.BitmapFactory
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import com.xiaojiaoyin.baby.ui.common.rememberProUnlocked
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId

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

    val filtered = if (query.isBlank()) photos
    else photos.filter { it.note.contains(query.trim(), ignoreCase = true) }
    val grouped = filtered.groupBy { monthLabel(it.occurredAt) }

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
                placeholder = { Text("搜索照片备注…", fontSize = 13.sp, color = TextSecondary) },
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
                text = "＋ 添加",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Mint,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable { picker.launch(arrayOf("*/*")) }
            )
        }

        if (grouped.isEmpty()) {
            Text(
                text = if (photos.isEmpty()) "还没有照片，点右上角添加" else "没有匹配的备注",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 70.dp)
            ) {
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
            onSaveNote = { note ->
                scope.launch {
                    AppGraph.recordRepository.update(previewRecord.copy(note = note.trim()))
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
private fun PhotoGridItem(
    context: android.content.Context,
    record: RecordEntity,
    onClick: () -> Unit
) {
    val path = record.detailJsonPath()
    val bitmap = remember(record.id) {
        if (path.isBlank()) null
        else BitmapFactory.decodeFile(PhotoStorage.loadFile(context, path).absolutePath)
    }
    Box(
        modifier = Modifier
            .padding(3.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE3EEE8))
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
    onSaveNote: (String) -> Unit
) {
    var note by remember(record.id) { mutableStateOf(record.note) }
    val shareContext = context
    val path = record.detailJsonPath()
    val bitmap = remember(record.id) {
        if (path.isBlank()) null
        else BitmapFactory.decodeFile(PhotoStorage.loadFile(context, path).absolutePath)
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
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("备注，如 第一次笑出声", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
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
            TextButton(onClick = { onSaveNote(note) }) { Text("保存备注") }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    if (path.isNotBlank()) {
                        val file = PhotoStorage.loadFile(shareContext, path)
                        val uri = FileProvider.getUriForFile(
                            shareContext,
                            "${shareContext.packageName}.fileprovider",
                            file
                        )
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        shareContext.startActivity(Intent.createChooser(share, "分享照片"))
                    }
                }) { Text("分享", color = Mint) }
                TextButton(onClick = onDelete) { Text("删除", color = Color(0xFFD96A6A)) }
            }
        }
    )
}

private fun RecordEntity.detailJsonPath(): String =
    runCatching { JSONObject(detailJson).optString("path", "") }.getOrDefault("")

private fun monthLabel(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.year}年${t.monthValue}月"
}

private fun timeLabel(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return String.format("%02d:%02d", t.hour, t.minute)
}
