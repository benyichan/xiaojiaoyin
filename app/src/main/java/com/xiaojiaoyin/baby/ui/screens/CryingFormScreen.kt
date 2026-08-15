package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.SegmentedField
import com.xiaojiaoyin.baby.ui.components.TextInputField
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.json.JSONObject
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryingFormScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var reasonIndex by remember { mutableStateOf(0) }
    var occurredAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var comfort by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showTime by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("记录哭闹", onBack)
        SegmentedField(
            label = "原因",
            options = listOf("饿了", "困了", "不舒服", "其他"),
            selected = reasonIndex,
            onSelect = { reasonIndex = it }
        )
        FormField(
            label = "时间",
            value = Instant.ofEpochMilli(occurredAt)
                .atZone(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
            onClick = { showTime = true }
        )
        TextInputField(
            label = "安抚方式",
            value = comfort,
            onValueChange = { comfort = it },
            placeholder = "如 抱起轻拍，10 分钟后安静"
        )
        TextInputField(
            label = "备注",
            value = note,
            onValueChange = { note = it },
            placeholder = "选填"
        )
        error?.let {
            Text(it, fontSize = 12.sp, color = Color(0xFFD96A6A), modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        SaveButton("保存") {
            if (comfort.isBlank()) {
                error = "请填写安抚方式"
                return@SaveButton
            }
            val detail = JSONObject()
                .put("reason", listOf("饿了", "困了", "不舒服", "其他")[reasonIndex])
                .put("comfort", comfort.trim())
            scope.launch {
                AppGraph.recordRepository.add(
                    babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository) ?: 0L,
                    type = RecordType.CRYING,
                    occurredAt = occurredAt,
                    detailJson = detail.toString(),
                    note = note.trim()
                )
                onBack()
            }
        }
    }

    if (showTime) {
        val t = Instant.ofEpochMilli(occurredAt).atZone(ZoneId.of("Asia/Shanghai"))
        val timeState = rememberTimePickerState(initialHour = t.hour, initialMinute = t.minute)
        TimePickerDialog(
            onDismissRequest = { showTime = false },
            title = { Text("选择时间") },
            confirmButton = {
                TextButton(onClick = {
                    occurredAt = t.toLocalDate().atTime(timeState.hour, timeState.minute)
                        .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli()
                    showTime = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showTime = false }) { Text("取消") } }
        ) {
            TimePicker(state = timeState)
        }
    }
}
