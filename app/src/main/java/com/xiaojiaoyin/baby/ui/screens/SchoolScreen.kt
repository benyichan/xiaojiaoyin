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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.xiaojiaoyin.baby.data.db.entity.SchoolStageEntity
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.ProLockedView
import com.xiaojiaoyin.baby.ui.common.rememberProUnlocked
import com.xiaojiaoyin.baby.ui.theme.Blue
import com.xiaojiaoyin.baby.ui.theme.BlueLight
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.GoldLight
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.Pink
import com.xiaojiaoyin.baby.ui.theme.PinkLight
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun SchoolScreen(onBack: () -> Unit, onAdd: () -> Unit, onUpgrade: () -> Unit) {
    val unlocked by rememberProUnlocked()
    if (!unlocked) {
        ProLockedView(
            title = "学籍信息",
            desc = "幼儿园到大学的学习生涯记录是 Pro 专属功能",
            onUpgrade = onUpgrade
        )
        return
    }
    val scope = rememberCoroutineScope()
    val babyId by com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId()
    val stages by remember(babyId) {
        if (babyId == null) flowOf(emptyList<SchoolStageEntity>())
        else AppGraph.schoolStageRepository.observeAll(babyId!!)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    var previewId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        OverlayHeader("学籍信息", onBack)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(Mint, RoundedCornerShape(14.dp))
                .clickable(onClick = onAdd)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("＋ 添加阶段", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (stages.isEmpty()) {
            Text(
                "还没有学籍阶段，从幼儿园开始记录吧",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 30.dp)
            ) {
                items(stages.size) { index ->
                    val s = stages[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .background(Card, RoundedCornerShape(18.dp))
                            .clickable { previewId = s.id }
                            .padding(13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StageBadge(s.stageType)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                "${s.className} · ${s.schoolName}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "${s.teacher} · ${s.startAt} - ${s.endAt}",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            if (s.costYuan > 0) {
                                Text(
                                    "学费 ¥%.2f / 年".format(s.costYuan),
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val preview = stages.firstOrNull { it.id == previewId }
    if (preview != null) {
        AlertDialog(
            onDismissRequest = { previewId = null },
            title = { Text("${preview.stageType} · ${preview.className}", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(preview.schoolName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("老师：${preview.teacher}", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                    Text("${preview.startAt} - ${preview.endAt}", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                    if (preview.studentNo.isNotBlank()) {
                        Text("学号：${preview.studentNo}", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                    }
                    if (preview.costYuan > 0) {
                        Text(
                            "学费 ¥%.2f / 年".format(preview.costYuan),
                            fontSize = 12.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (preview.note.isNotBlank()) {
                        Text(preview.note, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { previewId = null }) { Text("关闭") }
            },
            dismissButton = {
                TextButton(onClick = {
                    scope.launch { AppGraph.schoolStageRepository.delete(preview) }
                    previewId = null
                }) { Text("删除", color = Color(0xFFD96A6A)) }
            }
        )
    }
}

@Composable
private fun StageBadge(type: String) {
    val (bg, fg) = when (type) {
        "幼儿园" -> MintLight to Mint
        "小学" -> BlueLight to Blue
        "初中" -> GoldLight to Gold
        "高中" -> PinkLight to Pink
        else -> BlueLight to Blue
    }
    Text(
        text = type,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = fg,
        modifier = Modifier
            .background(bg, RoundedCornerShape(9.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp)
    )
}
