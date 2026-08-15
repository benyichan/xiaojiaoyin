package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.domain.BmiCalculator
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.json.JSONObject
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthFormScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var occurredAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var showTime by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val heightVal = height.toDoubleOrNull()
    val weightVal = weight.toDoubleOrNull()
    val bmiPreview = if (heightVal != null && weightVal != null && heightVal > 0 && weightVal > 0) {
        BmiCalculator.bmi(weightVal, heightVal)
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("记录生长", onBack)
        TextInputField(
            label = "身高（cm）",
            value = height,
            onValueChange = { height = it },
            placeholder = "如 62"
        )
        TextInputField(
            label = "体重（kg）",
            value = weight,
            onValueChange = { weight = it },
            placeholder = "如 6.8"
        )
        FormField(
            label = "时间",
            value = Instant.ofEpochMilli(occurredAt)
                .atZone(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
            onClick = { showTime = true }
        )
        bmiPreview?.let {
            Text(
                text = "BMI ${"%.1f".format(it)} · ${BmiCalculator.evaluate(weightVal!!, heightVal!!)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Mint,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(MintLight, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            )
        }
        error?.let {
            Text(it, fontSize = 12.sp, color = Color(0xFFD96A6A), modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        SaveButton("保存") {
            if (heightVal == null || weightVal == null || heightVal <= 0 || weightVal <= 0) {
                error = "请填写有效的身高和体重"
                return@SaveButton
            }
            val detail = JSONObject()
                .put("heightCm", heightVal)
                .put("weightKg", weightVal)
                .put("bmi", bmiPreview)
                .put("evaluation", BmiCalculator.evaluate(weightVal, heightVal))
            scope.launch {
                AppGraph.recordRepository.add(
                    babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository) ?: 0L,
                    type = RecordType.GROWTH,
                    occurredAt = occurredAt,
                    detailJson = detail.toString()
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
