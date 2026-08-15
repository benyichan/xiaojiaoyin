package com.xiaojiaoyin.baby.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.backup.BackupManager
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun BackupScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backupManager = remember { BackupManager(context.applicationContext) }
    var message by remember { mutableStateOf<String?>(null) }
    var confirmRestore by remember { mutableStateOf(false) }
    var restoreUri by remember { mutableStateOf<Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                message = runCatching {
                    val s = backupManager.export(uri)
                    "备份完成：${s.babies} 个宝宝、${s.records} 条记录"
                }.getOrElse { "备份失败：${it.message}" }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            restoreUri = uri
            confirmRestore = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("备份与恢复", onBack)
        BackupAction("导出备份", "生成包含全部宝宝与记录的文件，可保存到手机或电脑") {
            exportLauncher.launch("baby-app-backup-${System.currentTimeMillis()}.zip")
        }
        BackupAction("从备份恢复", "选择备份文件，将覆盖当前所有数据") {
            importLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
        }
        message?.let {
            Text(
                text = it,
                fontSize = 13.sp,
                color = if (it.startsWith("备份完成")) Mint else Color(0xFFD96A6A),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }

    if (confirmRestore && restoreUri != null) {
        AlertDialog(
            onDismissRequest = { confirmRestore = false },
            title = { Text("确认恢复") },
            text = { Text("恢复将覆盖当前所有数据，且应用会重启。继续吗？") },
            confirmButton = {
                TextButton(onClick = {
                    confirmRestore = false
                    scope.launch {
                        message = runCatching {
                            val s = backupManager.restore(restoreUri!!)
                            backupManager.restartApp()
                            "恢复完成：${s.babies} 个宝宝、${s.records} 条记录"
                        }.getOrElse { "恢复失败：${it.message}" }
                    }
                }) { Text("覆盖并恢复") }
            },
            dismissButton = {
                TextButton(onClick = { confirmRestore = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun BackupAction(title: String, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(Card, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(desc, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
