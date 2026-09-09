package com.xiaojiaoyin.baby.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.PhotoStorage
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.reminder.BirthdayScheduler
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
import com.xiaojiaoyin.baby.data.settings.ProStatusRepository
import com.xiaojiaoyin.baby.ui.theme.Red
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyEditScreen(babyId: Long?, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var editingBaby by remember { mutableStateOf<BabyEntity?>(null) }
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var genderIndex by remember { mutableStateOf(0) }
    var birthMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var avatarPath by remember { mutableStateOf("") }
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val zone = ZoneId.of("Asia/Shanghai")
    val birthTime = Instant.ofEpochMilli(birthMillis).atZone(zone)

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val newPath = PhotoStorage.saveAvatar(context, uri)
            if (newPath != null) {
                if (avatarPath.isNotEmpty()) PhotoStorage.delete(context, avatarPath)
                avatarPath = newPath
            }
        }
    }

    LaunchedEffect(babyId) {
        if (babyId != null) {
            val baby = AppGraph.babyRepository.getById(babyId)
            if (baby != null) {
                editingBaby = baby
                name = baby.name
                nickname = baby.nickname
                genderIndex = if (baby.gender == "男") 1 else 0
                birthMillis = baby.birthDateTime
                avatarPath = baby.avatarPath
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .imePadding().navigationBarsPadding().padding(bottom = 30.dp)
    ) {
        OverlayHeader(if (babyId == null) "添加宝宝" else "编辑档案", onBack)

        // 头像：点击从相册选择；无照片时显示名字首字
        val avatarBmp = if (avatarPath.isNotEmpty()) {
            remember(avatarPath) {
                PhotoStorage.decodeThumb(PhotoStorage.loadFile(context, avatarPath), 160)
            }
        } else null
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clickable { avatarPicker.launch(arrayOf("image/*")) },
                contentAlignment = Alignment.Center
            ) {
                if (avatarBmp != null) {
                    Image(
                        bitmap = avatarBmp.asImageBitmap(),
                        contentDescription = "头像",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
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
                }
            }
            Text(
                text = if (avatarPath.isEmpty()) "点击设置头像" else "点击更换头像",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
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
                color = Red,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(Mint, RoundedCornerShape(14.dp))
                .clickable {
                    if (name.isBlank()) {
                        error = "请填写姓名"
                        return@clickable
                    }
                    scope.launch {
                        if (editingBaby != null) {
                            val updated = editingBaby!!.copy(
                                name = name.trim(),
                                nickname = nickname.trim(),
                                gender = if (genderIndex == 0) "女" else "男",
                                birthDateTime = birthMillis,
                                avatarPath = avatarPath
                            )
                            AppGraph.babyRepository.update(updated)
                            BirthdayScheduler(context.applicationContext).scheduleForBaby(updated)
                            runCatching { com.xiaojiaoyin.baby.reminder.VaccineScheduler(context.applicationContext).scheduleForBaby(updated) }
                        } else {
                            val existing = AppGraph.babyRepository.getAll()
                            val isPro = AppGraph.proStatusRepository.isPro.first()
                            val trialStart = AppGraph.proStatusRepository.trialStartAt.first()
                            val unlocked = isPro || (
                                trialStart > 0 &&
                                    System.currentTimeMillis() <
                                    trialStart + ProStatusRepository.TRIAL_DAYS * ProStatusRepository.DAY_MS
                                )
                            if (existing.isNotEmpty() && !unlocked) {
                                error = "免费版只能记录一个宝宝，升级 Pro 可添加多个"
                                return@launch
                            }
                            val baby = BabyEntity(
                                name = name.trim(),
                                nickname = nickname.trim(),
                                gender = if (genderIndex == 0) "女" else "男",
                                birthDateTime = birthMillis,
                                avatarColorIndex = 0,
                                avatarPath = avatarPath,
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )
                            val id = AppGraph.babyRepository.add(baby)
                            BirthdayScheduler(context.applicationContext).scheduleForBaby(baby.copy(id = id))
                            runCatching { com.xiaojiaoyin.baby.reminder.VaccineScheduler(context.applicationContext).scheduleForBaby(baby.copy(id = id)) }
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
                        // 出生时间不能在未来：选了未来日期则钳制为今天
                        val today = java.time.LocalDate.now(zone)
                        val clamped = if (date.isAfter(today)) today else date
                        val old = Instant.ofEpochMilli(birthMillis).atZone(zone)
                        birthMillis = clamped.atTime(old.hour, old.minute).atZone(zone).toInstant().toEpochMilli()
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
