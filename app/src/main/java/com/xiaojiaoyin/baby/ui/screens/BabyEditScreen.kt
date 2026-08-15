package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.SegmentedField
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyEditScreen(babyId: Long?, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var editingBaby by remember { mutableStateOf<BabyEntity?>(null) }
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var genderIndex by remember { mutableStateOf(0) }
    var birthMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val zone = ZoneId.of("Asia/Shanghai")
    val birthTime = Instant.ofEpochMilli(birthMillis).atZone(zone)

    LaunchedEffect(babyId) {
        if (babyId != null) {
            val baby = AppGraph.babyRepository.getById(babyId)
            if (baby != null) {
                editingBaby = baby
                name = baby.name
                nickname = baby.nickname
                genderIndex = if (baby.gender == "男") 1 else 0
                birthMillis = baby.birthDateTime
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
        OverlayHeader(if (babyId == null) "添加宝宝" else "编辑档案", onBack)

        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .size(72.dp)
                .align(Alignment.CenterHorizontally)
                .background(MintLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).ifEmpty { "宝" },
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Mint
            )
        }

        TextInputField(
            label = "姓名 *",
            value = name,
            onValueChange = { name = it }
        )
        TextInputField(
            label = "小名",
            value = nickname,
            onValueChange = { nickname = it }
        )
        SegmentedField(
            label = "性别",
            options = listOf("女宝", "男宝"),
            selected = genderIndex,
            onSelect = { genderIndex = it }
        )
        FormField(
            label = "出生日期",
            value = birthTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            onClick = { showDate = true }
        )
        FormField(
            label = "出生时间",
            value = birthTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            onClick = { showTime = true }
        )

        error?.let {
            Text(
                text = it,
                fontSize = 12.sp,
                color = androidx.compose.ui.graphics.Color(0xFFD96A6A),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(Mint, RoundedCornerShape(16.dp))
                .clickable {
                    if (name.isBlank()) {
                        error = "请填写姓名"
                        return@clickable
                    }
                    scope.launch {
                        if (editingBaby != null) {
                            AppGraph.babyRepository.update(
                                editingBaby!!.copy(
                                    name = name.trim(),
                                    nickname = nickname.trim(),
                                    gender = if (genderIndex == 0) "女" else "男",
                                    birthDateTime = birthMillis
                                )
                            )
                        } else {
                            val existing = AppGraph.babyRepository.getAll()
                            val isPro = AppGraph.proStatusRepository.isPro.first()
                            if (existing.isNotEmpty() && !isPro) {
                                error = "免费版只能记录一个宝宝，升级 Pro 可添加多个"
                                return@launch
                            }
                            val id = AppGraph.babyRepository.add(
                                BabyEntity(
                                    name = name.trim(),
                                    nickname = nickname.trim(),
                                    gender = if (genderIndex == 0) "女" else "男",
                                    birthDateTime = birthMillis,
                                    avatarColorIndex = 0,
                                    createdAt = System.currentTimeMillis()
                                )
                            )
                            if (AppGraph.settingsRepository.getCurrentBabyId() == null) {
                                AppGraph.settingsRepository.setCurrentBaby(id)
                            }
                        }
                        onBack()
                    }
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "保存",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }

    if (showDate) {
        val dateState = rememberDatePickerState(initialSelectedDateMillis = birthMillis)
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { selected ->
                        val date = Instant.ofEpochMilli(selected).atZone(zone).toLocalDate()
                        val old = Instant.ofEpochMilli(birthMillis).atZone(zone)
                        birthMillis = date.atTime(old.hour, old.minute).atZone(zone).toInstant().toEpochMilli()
                    }
                    showDate = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showDate = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    if (showTime) {
        val timeState = rememberTimePickerState(
            initialHour = birthTime.hour,
            initialMinute = birthTime.minute
        )
        TimePickerDialog(
            onDismissRequest = { showTime = false },
            title = { Text("选择出生时间") },
            confirmButton = {
                TextButton(onClick = {
                    val old = Instant.ofEpochMilli(birthMillis).atZone(zone)
                    birthMillis = old.toLocalDate()
                        .atTime(timeState.hour, timeState.minute)
                        .atZone(zone)
                        .toInstant()
                        .toEpochMilli()
                    showTime = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showTime = false }) { Text("取消") }
            }
        ) {
            TimePicker(state = timeState)
        }
    }
}
