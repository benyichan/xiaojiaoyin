package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.AnniversaryEntity
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.SegmentedField
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.theme.Red
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnniversaryFormScreen(editId: Long?, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var editing by remember { mutableStateOf<AnniversaryEntity?>(null) }
    var name by remember { mutableStateOf("") }
    var dateAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var repeatIndex by remember { mutableStateOf(0) }
    var note by remember { mutableStateOf("") }
    var showDate by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(editId) {
        if (editId != null) {
            AppGraph.anniversaryRepository.getById(editId)?.let {
                editing = it
                name = it.name
                dateAt = it.dateAt
                repeatIndex = if (it.repeatYearly) 0 else 1
                note = it.note
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
        OverlayHeader(if (editId == null) "新建纪念日" else "编辑纪念日", onBack)
        TextInputField(
            label = "名称 *",
            value = name,
            onValueChange = { name = it },
            placeholder = "如 百天宴、周岁宴、预产期"
        )
        FormField(
            label = "日期",
            value = Instant.ofEpochMilli(dateAt).atZone(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            onClick = { showDate = true }
        )
        SegmentedField(
            label = "重复",
            options = listOf("每年重复", "一次性"),
            selected = repeatIndex,
            onSelect = { repeatIndex = it }
        )
        TextInputField(
            label = "备注",
            value = note,
            onValueChange = { note = it },
            placeholder = "选填"
        )
        error?.let {
            Text(it, fontSize = 12.sp, color = Red, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        SaveButton("保存") {
            if (name.isBlank()) {
                error = "请填写名称"
                return@SaveButton
            }
            scope.launch {
                val babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository) ?: 0L
                if (editing != null) {
                    AppGraph.anniversaryRepository.update(
                        editing!!.copy(
                            name = name.trim(),
                            dateAt = dateAt,
                            repeatYearly = repeatIndex == 0,
                            note = note.trim()
                        )
                    )
                } else {
                    AppGraph.anniversaryRepository.add(
                        babyId = babyId,
                        name = name.trim(),
                        dateAt = dateAt,
                        repeatYearly = repeatIndex == 0,
                        note = note.trim()
                    )
                }
                onBack()
            }
        }
        if (editing != null) {
            Text(
                text = "删除该纪念日：长按列表项",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

    if (showDate) {
        val dateState = rememberDatePickerState(initialSelectedDateMillis = dateAt)
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let {
                        // DatePicker 返回 UTC 0 点，转上海时区当天 12:00 避免时区偏移跨天
                        val d = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        dateAt = d.atTime(12, 0).atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli()
                    }
                    showDate = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("取消") } }
        ) {
            DatePicker(state = dateState)
        }
    }
}
