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
import androidx.compose.material3.Text
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.SegmentedField
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.theme.Mint
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.xiaojiaoyin.baby.ui.theme.Red
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsFormScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var categoryIndex by remember { mutableStateOf(0) }
    var price by remember { mutableStateOf("") }
    var ratingIndex by remember { mutableStateOf(4) }
    var note by remember { mutableStateOf("") }
    var buyDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDate by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val zone = ZoneId.of("Asia/Shanghai")
    val categories = listOf("玩具", "喂养", "衣物", "出行", "医疗", "其他")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .imePadding().navigationBarsPadding().padding(bottom = 30.dp)
    ) {
        OverlayHeader("添加好物", onBack)
        TextInputField(
            label = "名称 *",
            value = name,
            onValueChange = { name = it },
            placeholder = "如 婴儿推车、恒温壶"
        )
        SegmentedField(
            label = "分类",
            options = categories,
            selected = categoryIndex,
            onSelect = { categoryIndex = it }
        )
        TextInputField(
            label = "价格（元）",
            value = price,
            onValueChange = { price = it },
            placeholder = "如 899"
        )
        SegmentedField(
            label = "评分",
            options = listOf("★", "★★", "★★★", "★★★★", "★★★★★"),
            selected = ratingIndex,
            onSelect = { ratingIndex = it }
        )
        FormField(
            label = "购买日期",
            value = Instant.ofEpochMilli(buyDate).atZone(zone)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            onClick = { showDate = true }
        )
        TextInputField(
            label = "备注",
            value = note,
            onValueChange = { note = it },
            placeholder = "为什么好用"
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
                    if (name.isBlank()) {
                        error = "请填写名称"
                        return@clickable
                    }
                    scope.launch {
                        val babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
                        if (babyId != null) {
                            AppGraph.goodItemRepository.add(
                                babyId = babyId,
                                name = name.trim(),
                                category = categories[categoryIndex],
                                priceYuan = price.toDoubleOrNull() ?: 0.0,
                                rating = ratingIndex + 1,
                                note = note.trim(),
                                buyDate = buyDate
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
        val dateState = rememberDatePickerState(initialSelectedDateMillis = buyDate)
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { selected ->
                        val date = Instant.ofEpochMilli(selected).atZone(zone).toLocalDate()
                        val old = Instant.ofEpochMilli(buyDate).atZone(zone)
                        buyDate = date.atTime(old.hour, old.minute).atZone(zone).toInstant().toEpochMilli()
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
