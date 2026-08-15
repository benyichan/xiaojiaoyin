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
import androidx.compose.foundation.lazy.items
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
import com.xiaojiaoyin.baby.data.db.entity.GoodItemEntity
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun GoodsScreen(onBack: () -> Unit, onAdd: () -> Unit) {
    val scope = rememberCoroutineScope()
    val babyId by com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId()
    val items by remember(babyId) {
        if (babyId == null) flowOf(emptyList<GoodItemEntity>())
        else AppGraph.goodItemRepository.observeAll(babyId!!)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    var previewId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        OverlayHeader("好物清单", onBack)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(Mint, RoundedCornerShape(14.dp))
                .clickable(onClick = onAdd)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("＋ 添加好物", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (items.isEmpty()) {
            Text(
                "还没有好物，把用过觉得好的东西记下来吧",
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
                items(items.size) { index ->
                    val item = items[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .background(Card, RoundedCornerShape(18.dp))
                            .clickable { previewId = item.id }
                            .padding(13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 10.dp)
                        ) {
                            Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Row(modifier = Modifier.padding(top = 2.dp)) {
                                Text(item.category, fontSize = 10.sp, color = TextSecondary)
                                if (item.priceYuan > 0) {
                                    Text(
                                        "¥%.2f".format(item.priceYuan),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                            if (item.note.isNotBlank()) {
                                Text(item.note, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                        Text("★".repeat(item.rating), fontSize = 13.sp, color = Gold)
                    }
                }
            }
        }
    }

    val preview = items.firstOrNull { it.id == previewId }
    if (preview != null) {
        AlertDialog(
            onDismissRequest = { previewId = null },
            title = { Text(preview.name, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    if (preview.priceYuan > 0) {
                        Text("价格：¥%.2f".format(preview.priceYuan), fontSize = 13.sp, color = TextPrimary)
                    }
                    Text("分类：${preview.category}", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                    if (preview.buyDate > 0) {
                        Text(
                            "购买日期：${formatGoodDate(preview.buyDate)}",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Text("评分：${"★".repeat(preview.rating)}", fontSize = 13.sp, color = Gold)
                    if (preview.note.isNotBlank()) {
                        Text(preview.note, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { previewId = null }) { Text("关闭") }
            },
            dismissButton = {
                TextButton(onClick = {
                    scope.launch { AppGraph.goodItemRepository.delete(preview) }
                    previewId = null
                }) { Text("删除", color = Color(0xFFD96A6A)) }
            }
        )
    }
}

private fun formatGoodDate(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
