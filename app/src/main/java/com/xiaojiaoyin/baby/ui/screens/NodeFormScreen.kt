package com.xiaojiaoyin.baby.ui.screens

import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.PhotoStorage
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.common.rememberProUnlocked
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeFormScreen(nodeId: Long?, onBack: () -> Unit, onUpgrade: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val unlocked by rememberProUnlocked()
    val maxPhotos = if (unlocked) 3 else 1
    var editingNode by remember { mutableStateOf<com.xiaojiaoyin.baby.data.db.entity.RecordEntity?>(null) }
    var existingPhotos by remember { mutableStateOf<List<RecordEntity>>(emptyList()) }
    val selectedPaths = remember { mutableStateListOf<String>() }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var timeAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showUpgrade by remember { mutableStateOf(false) }
    val zone = ZoneId.of("Asia/Shanghai")

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            if (selectedPaths.size >= maxPhotos) {
                showUpgrade = true
            } else {
                scope.launch {
                    val path = PhotoStorage.saveImage(context, uri)
                    if (path != null && path !in selectedPaths) selectedPaths.add(path)
                }
            }
        }
    }

    LaunchedEffect(nodeId) {
        if (nodeId != null) {
            val babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
                ?: return@LaunchedEffect
            val node = AppGraph.recordRepository.observeByType(babyId, RecordType.NODE)
                .first().firstOrNull { it.id == nodeId }
            if (node != null) {
                editingNode = node
                title = node.title()
                note = node.note
                timeAt = node.occurredAt
                existingPhotos = AppGraph.recordRepository.getByParent(node.id)
                selectedPaths.clear()
                selectedPaths.addAll(existingPhotos.map { it.detailJsonPath() })
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader(if (nodeId == null) "记录成长节点" else "编辑节点", onBack)
        TextInputField(
            label = "标题 *",
            value = title,
            onValueChange = { title = it },
            placeholder = "如 第一次翻身、会喊妈妈"
        )
        FormField(
            label = "日期",
            value = Instant.ofEpochMilli(timeAt).atZone(zone)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            onClick = { showDate = true }
        )
        FormField(
            label = "时间",
            value = Instant.ofEpochMilli(timeAt).atZone(zone)
                .format(DateTimeFormatter.ofPattern("HH:mm")),
            onClick = { showTime = true }
        )
        TextInputField(
            label = "备注",
            value = note,
            onValueChange = { note = it },
            placeholder = "细节、当时的心情…"
        )
        Text(
            text = "配图（免费 1 张 / Pro 3 张）",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp)
        )
        if (selectedPaths.isNotEmpty()) {
            Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                selectedPaths.forEach { path ->
                    NodeFormPhotoThumb(context = context, path = path) {
                        selectedPaths.remove(path)
                    }
                }
            }
        }
        Text(
            text = if (selectedPaths.size >= maxPhotos)
                "已达上限（$maxPhotos 张），可在下方删除后重选"
            else
                "＋ 添加图片（${selectedPaths.size}/$maxPhotos）",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selectedPaths.size >= maxPhotos) TextSecondary else Mint,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clickable(enabled = selectedPaths.size < maxPhotos) {
                    picker.launch(arrayOf("image/*"))
                }
        )
        error?.let {
            Text(it, fontSize = 12.sp, color = Color(0xFFD96A6A), modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(Mint, RoundedCornerShape(16.dp))
                .clickable {
                    if (title.isBlank()) {
                        error = "请填写标题"
                        return@clickable
                    }
                    scope.launch {
                        if (editingNode != null) {
                            val node = editingNode!!
                            val keepPaths = selectedPaths.toSet()
                            existingPhotos.forEach { photo ->
                                if (photo.detailJsonPath() !in keepPaths) {
                                    PhotoStorage.delete(context, photo.detailJsonPath())
                                    AppGraph.recordRepository.delete(photo)
                                } else {
                                    AppGraph.recordRepository.updateWithTimestamp(
                                        photo.copy(occurredAt = timeAt)
                                    )
                                }
                            }
                            existingPhotos.map { it.detailJsonPath() }
                                .toSet()
                                .let { existingSet ->
                                    selectedPaths.filter { it !in existingSet }.forEach { path ->
                                        AppGraph.recordRepository.addNodePhoto(
                                            babyId = node.babyId,
                                            nodeId = node.id,
                                            path = path,
                                            occurredAt = timeAt,
                                            note = title.trim()
                                        )
                                    }
                                }
                            AppGraph.recordRepository.updateWithTimestamp(
                                node.copy(
                                    occurredAt = timeAt,
                                    note = note.trim(),
                                    detailJson = JSONObject().put("title", title.trim()).toString()
                                )
                            )
                        } else {
                            val babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
                            if (babyId != null) {
                                val nodeId = AppGraph.recordRepository.add(
                                    babyId = babyId,
                                    type = RecordType.NODE,
                                    occurredAt = timeAt,
                                    detailJson = JSONObject().put("title", title.trim()).toString(),
                                    note = note.trim()
                                )
                                selectedPaths.forEach { path ->
                                    AppGraph.recordRepository.addNodePhoto(
                                        babyId = babyId,
                                        nodeId = nodeId,
                                        path = path,
                                        occurredAt = timeAt,
                                        note = title.trim()
                                    )
                                }
                            }
                        }
                        onBack()
                    }
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("保存", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }

    if (showDate) {
        val dateState = rememberDatePickerState(initialSelectedDateMillis = timeAt)
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { selected ->
                        val date = Instant.ofEpochMilli(selected).atZone(zone).toLocalDate()
                        val old = Instant.ofEpochMilli(timeAt).atZone(zone)
                        timeAt = date.atTime(old.hour, old.minute).atZone(zone).toInstant().toEpochMilli()
                    }
                    showDate = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("取消") } }
        ) {
            DatePicker(state = dateState)
        }
    }

    if (showTime) {
        val t = Instant.ofEpochMilli(timeAt).atZone(zone)
        val timeState = rememberTimePickerState(initialHour = t.hour, initialMinute = t.minute)
        TimePickerDialog(
            onDismissRequest = { showTime = false },
            title = { Text("选择时间") },
            confirmButton = {
                TextButton(onClick = {
                    timeAt = t.toLocalDate().atTime(timeState.hour, timeState.minute)
                        .atZone(zone).toInstant().toEpochMilli()
                    showTime = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showTime = false }) { Text("取消") } }
        ) {
            TimePicker(state = timeState)
        }
    }

    if (showUpgrade) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showUpgrade = false },
            title = { Text("配图上限", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    if (unlocked) "每个节点最多 3 张配图，可删除后重新添加。"
                    else "免费版每个节点最多 1 张配图，升级 Pro 可添加 3 张。"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showUpgrade = false
                    if (!unlocked) onUpgrade()
                }) { Text(if (unlocked) "知道了" else "去升级", color = Mint) }
            },
            dismissButton = {
                TextButton(onClick = { showUpgrade = false }) { Text("暂不") }
            }
        )
    }
}

private fun RecordEntity.detailJsonPath(): String =
    runCatching { JSONObject(detailJson).optString("path", "") }.getOrDefault("")

@Composable
private fun NodeFormPhotoThumb(
    context: android.content.Context,
    path: String,
    onDelete: () -> Unit
) {
    val bitmap = remember(path) {
        if (path.isBlank()) null
        else BitmapFactory.decodeFile(PhotoStorage.loadFile(context, path).absolutePath)
    }
    Box {
        Box(
            modifier = Modifier
                .size(72.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE3EEE8))
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
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(3.dp)
                .size(20.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Text("×", fontSize = 13.sp, color = Color.White)
        }
    }
}
