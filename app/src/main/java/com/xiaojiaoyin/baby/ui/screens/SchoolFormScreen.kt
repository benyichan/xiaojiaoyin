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
import com.xiaojiaoyin.baby.ui.components.SegmentedField
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.theme.Mint
import kotlinx.coroutines.launch

@Composable
fun SchoolFormScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var typeIndex by remember { mutableStateOf(0) }
    var className by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var startAt by remember { mutableStateOf("") }
    var endAt by remember { mutableStateOf("") }
    var studentNo by remember { mutableStateOf("") }
    var costYuan by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val types = listOf("幼儿园", "小学", "初中", "高中", "大学", "其他")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("添加学籍阶段", onBack)
        SegmentedField(
            label = "阶段类型",
            options = types,
            selected = typeIndex,
            onSelect = { typeIndex = it }
        )
        TextInputField("班级 / 年级", className, onValueChange = { className = it })
        TextInputField("学校名称", schoolName, onValueChange = { schoolName = it })
        TextInputField("班主任 / 老师", teacher, onValueChange = { teacher = it })
        TextInputField("入学时间", startAt, onValueChange = { startAt = it }, placeholder = "如 2029-09")
        TextInputField("毕业时间", endAt, onValueChange = { endAt = it }, placeholder = "如 2030-06")
        TextInputField("学号", studentNo, onValueChange = { studentNo = it }, placeholder = "选填")
        TextInputField("学费 / 年（元）", costYuan, onValueChange = { costYuan = it }, placeholder = "如 8000（选填）")
        TextInputField("备注", note, onValueChange = { note = it }, placeholder = "校车、接送人等")
        error?.let {
            Text(it, fontSize = 12.sp, color = Color(0xFFD96A6A), modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(Mint, RoundedCornerShape(16.dp))
                .clickable {
                    if (schoolName.isBlank()) {
                        error = "请填写学校名称"
                        return@clickable
                    }
                    scope.launch {
                        val babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
                        if (babyId != null) {
                            AppGraph.schoolStageRepository.add(
                                babyId = babyId,
                                stageType = types[typeIndex],
                                className = className.trim(),
                                schoolName = schoolName.trim(),
                                teacher = teacher.trim(),
                                startAt = startAt.trim(),
                                endAt = endAt.trim(),
                                studentNo = studentNo.trim(),
                                costYuan = costYuan.toDoubleOrNull() ?: 0.0,
                                note = note.trim()
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
}
