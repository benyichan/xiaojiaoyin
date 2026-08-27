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
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.SegmentedField
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintDeep
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.json.JSONObject
import kotlinx.coroutines.launch
import com.xiaojiaoyin.baby.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingFormScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var kindIndex by remember { mutableStateOf(0) }
    var occurredAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showTime by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val timerStart by AppGraph.settingsRepository.feedingTimerStart
        .collectAsStateWithLifecycle(initialValue = null)
    var nowTick by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(timerStart != null) {
        if (timerStart != null) {
            while (true) {
                nowTick = System.currentTimeMillis()
                kotlinx.coroutines.delay(1000)
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
        OverlayHeader("记录喂养", onBack)
        // 喂养计时器：开始 → 实时时长 → 停止并自动填入时长（开始时刻作为记录时间）
        val ts = timerStart
        if (ts != null) {
            val elapsed = (nowTick - ts).coerceAtLeast(0)
            val mm = elapsed / 60000
            val ss = (elapsed / 1000) % 60
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .background(MintLight, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "计时中 %02d:%02d".format(mm, ss),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDeep,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "停止并填入",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(Mint, RoundedCornerShape(50.dp))
                        .clickable {
                            val minutes = (elapsed / 60000).toInt().coerceAtLeast(1)
                            amount = minutes.toString()
                            occurredAt = ts
                            scope.launch { AppGraph.settingsRepository.stopFeedingTimer() }
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        } else {
            Text(
                text = "开始计时（喂完自动填入时长）",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Mint,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .background(MintLight, RoundedCornerShape(14.dp))
                    .clickable {
                        scope.launch {
                            AppGraph.settingsRepository.startFeedingTimer()
                            occurredAt = System.currentTimeMillis()
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            )
        }
        SegmentedField(
            label = "类型",
            options = listOf("母乳", "奶粉", "辅食"),
            selected = kindIndex,
            onSelect = { kindIndex = it }
        )
        FormField(
            label = "时间",
            value = Instant.ofEpochMilli(occurredAt)
                .atZone(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
            onClick = { showTime = true }
        )
        TextInputField(
            label = if (kindIndex == 0) "时长（分钟）" else "奶量（ml）",
            value = amount,
            onValueChange = { amount = it },
            placeholder = if (kindIndex == 0) "如 15" else "如 120"
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
            if (amount.isBlank()) {
                error = "请填写时长或奶量"
                return@SaveButton
            }
            val detail = JSONObject()
                .put("kind", listOf("母乳", "奶粉", "辅食")[kindIndex])
                .put("amount", amount.trim())
            scope.launch {
                AppGraph.recordRepository.add(
                    babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository) ?: 0L,
                    type = RecordType.FEEDING,
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

@Composable
fun SaveButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .background(Mint, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
    }
}
