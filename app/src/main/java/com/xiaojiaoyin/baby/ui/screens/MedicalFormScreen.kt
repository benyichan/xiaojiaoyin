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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.SegmentedField
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.theme.Mint
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.xiaojiaoyin.baby.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalFormScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var categoryIndex by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var dateAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDate by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val zone = ZoneId.of("Asia/Shanghai")
    val categories = listOf("疫苗", "体检", "就诊", "用药")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("记录医疗", onBack)
        SegmentedField(
            label = "分类",
            options = categories,
            selected = categoryIndex,
            onSelect = { categoryIndex = it }
        )
        TextInputField(
            label = "标题 *",
            value = title,
            onValueChange = { title = it },
            placeholder = "如 乙肝疫苗第二针、3个月体检"
        )
        FormField(
            label = "日期",
            value = Instant.ofEpochMilli(dateAt).atZone(zone)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            onClick = { showDate = true }
        )
        TextInputField(
            label = "详情",
            value = detail,
            onValueChange = { detail = it },
            placeholder = "医生建议、用药量等"
        )
        TextInputField(
            label = "费用（元）",
            value = cost,
            onValueChange = { cost = it },
            placeholder = "如 120（选填）"
        )
        error?.let {
            Text(it, fontSize = 12.sp, color = Red, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(Mint, RoundedCornerShape(14.dp))
                .clickable {
                    if (title.isBlank()) {
                        error = "请填写标题"
                        return@clickable
                    }
                    scope.launch {
                        val babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
                        if (babyId != null) {
                            AppGraph.recordRepository.addMedical(
                                babyId = babyId,
                                category = categories[categoryIndex],
                                title = title.trim(),
                                detail = detail.trim(),
                                costYuan = cost.toDoubleOrNull() ?: 0.0,
                                occurredAt = dateAt
                            )
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
        val dateState = rememberDatePickerState(initialSelectedDateMillis = dateAt)
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { selected ->
                        val date = Instant.ofEpochMilli(selected).atZone(zone).toLocalDate()
                        val old = Instant.ofEpochMilli(dateAt).atZone(zone)
                        dateAt = date.atTime(old.hour, old.minute).atZone(zone).toInstant().toEpochMilli()
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
