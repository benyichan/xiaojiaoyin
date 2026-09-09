package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.PhotoStorage
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.reminder.BirthdayScheduler
import com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId
import com.xiaojiaoyin.baby.ui.common.rememberProUnlocked
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.GoldLight
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import com.xiaojiaoyin.baby.ui.theme.Red
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun BabyManageScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDetail: (Long) -> Unit,
    onAdd: () -> Unit,
    onUpgrade: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val babies by AppGraph.babyRepository.observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val currentBabyId by rememberCurrentBabyId()
    val unlocked by rememberProUnlocked()
    var deleteTarget by remember { mutableStateOf<BabyEntity?>(null) }
    var showUpgrade by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        OverlayHeader("宝宝管理", onBack)
        Text(
            "点击宝宝设为当前，可编辑或删除",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(Mint, RoundedCornerShape(14.dp))
                .clickable {
                    if (!unlocked && babies.size >= 1) showUpgrade = true else onAdd()
                }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("＋ 添加宝宝", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding().padding(start = 16.dp, end = 16.dp, bottom = 30.dp)
        ) {
            items(babies.size) { index ->
                val baby = babies[index]
                val isCurrent = baby.id == currentBabyId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .background(Card, RoundedCornerShape(18.dp))
                        .clickable {
                            scope.launch { AppGraph.settingsRepository.setCurrentBaby(baby.id) }
                        }
                        .padding(13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MintLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(baby.name.take(1), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Mint)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(baby.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            "${baby.gender}宝 · 小名 ${baby.nickname.ifBlank { "未填" }}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    if (isCurrent) {
                        Text(
                            "当前",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Mint,
                            modifier = Modifier
                                .background(MintLight, RoundedCornerShape(9.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        "详情",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { onDetail(baby.id) }
                    )
                    Text(
                        "编辑",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Mint,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { onEdit(baby.id) }
                    )
                    Text(
                        "删除",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Red,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable { deleteTarget = baby }
                    )
                }
            }
        }
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("删除宝宝", fontWeight = FontWeight.Bold) },
            text = { Text("确定删除「${target.name}」？该宝宝的全部记录、照片、待办等数据将一并删除，且无法恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        // 级联删除：事务内删全部子表数据，随后清理照片文件
                        val photoPaths = AppGraph.babyRepository.deleteCascade(target)
                        photoPaths.forEach { PhotoStorage.delete(context, it) }
                        if (target.avatarPath.isNotBlank()) PhotoStorage.delete(context, target.avatarPath)
                        BirthdayScheduler(context.applicationContext).cancelForBaby(target.id)
                        runCatching { com.xiaojiaoyin.baby.reminder.VaccineScheduler(context.applicationContext).cancelForBaby(target.id) }
                        if (target.id == currentBabyId) {
                            val rest = babies.filter { it.id != target.id }
                            rest.firstOrNull()?.let { AppGraph.settingsRepository.setCurrentBaby(it.id) }
                        }
                    }
                    deleteTarget = null
                }) { Text("删除", color = Red) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("取消") }
            }
        )
    }

    if (showUpgrade) {
        AlertDialog(
            onDismissRequest = { showUpgrade = false },
            title = { Text("升级 Pro", fontWeight = FontWeight.Bold) },
            text = { Text("免费版只能记录一个宝宝。") },
            confirmButton = {
                TextButton(onClick = {
                    showUpgrade = false
                    onUpgrade()
                }) { Text("去升级", color = Mint) }
            },
            dismissButton = {
                TextButton(onClick = { showUpgrade = false }) { Text("暂不") }
            }
        )
    }
}
