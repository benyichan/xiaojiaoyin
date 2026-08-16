package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.theme.Mint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeFormScreen(nodeId: Long?, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var editingNode by remember { mutableStateOf<com.xiaojiaoyin.baby.data.db.entity.RecordEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var timeAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val zone = ZoneId.of("Asia/Shanghai")

    LaunchedEffect(nodeId) {
        if (nodeId != null) {
            val node = AppGraph.recordRepository.observeByType(
                AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository) ?: return@LaunchedEffect,
                com.xiaojiaoyin.baby.data.db.entity.RecordType.NODE
            ).first().firstOrNull { it.id == nodeId }
            if (node != null) {
                editingNode = node
                title = node.title()
                note = node.note
                timeAt = node.occurredAt
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
                            AppGraph.recordRepository.updateWithTimestamp(
                                editingNode!!.copy(
                                    occurredAt = timeAt,
                                    note = note.trim(),
                                    detailJson = JSONObject().put("title", title.trim()).toString()
                                )
                            )
                        } else {
                            val babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
                            if (babyId != null) {
                                AppGraph.recordRepository.add(
                                    babyId = babyId,
                                    type = RecordType.NODE,
                                    occurredAt = timeAt,
                                    detailJson = JSONObject().put("title", title.trim()).toString(),
                                    note = note.trim()
                                )
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
}
