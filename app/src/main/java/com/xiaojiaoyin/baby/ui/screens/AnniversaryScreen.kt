package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.AnniversaryEntity
import com.xiaojiaoyin.baby.domain.AnniversaryCalc
import com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.Red
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnniversaryScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    val babyId by rememberCurrentBabyId()
    val items by remember(babyId) {
        if (babyId == null) kotlinx.coroutines.flow.flowOf(emptyList<AnniversaryEntity>())
        else AppGraph.anniversaryRepository.observeByBaby(babyId!!)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    var pendingDelete by remember { mutableStateOf<AnniversaryEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("纪念日", onBack)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(Mint, RoundedCornerShape(14.dp))
                .clickable { onAdd() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("＋ 新建纪念日", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
        }

        if (items.isEmpty()) {
            Text(
                "还没有纪念日，点上面新建一个（如 百天宴、周岁宴）",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items.size) { idx ->
                    val item = items[idx]
                    val now = System.currentTimeMillis()
                    val countdown = AnniversaryCalc.countdownText(item.dateAt, item.repeatYearly, now)
                    val days = AnniversaryCalc.daysUntil(item.dateAt, item.repeatYearly, now)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Card, RoundedCornerShape(18.dp))
                            .combinedClickable(
                                onClick = { onEdit(item.id) },
                                onLongClick = { pendingDelete = item }
                            )
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                item.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                countdown,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier
                                    .background(
                                        if (days in 0..7) Gold else Mint,
                                        RoundedCornerShape(50.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = dateFormat(item.dateAt) + if (item.repeatYearly) " · 每年重复" else " · 一次性",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        if (item.note.isNotBlank()) {
                            Text(
                                item.note,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }

    pendingDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除纪念日", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = { Text("确定删除「${item.name}」吗？") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { AppGraph.anniversaryRepository.delete(item) }
                    pendingDelete = null
                }) { Text("删除", color = Red) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("取消") } }
        )
    }
}

private fun dateFormat(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
