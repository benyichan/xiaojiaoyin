package com.xiaojiaoyin.baby.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.sync.SyncManager
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.sync.SyncMerger
import com.xiaojiaoyin.baby.sync.SyncNetwork
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.ProLockedView
import com.xiaojiaoyin.baby.ui.common.rememberProUnlocked
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SyncScreen(onBack: () -> Unit, onUpgrade: () -> Unit) {
    val unlocked by rememberProUnlocked()
    if (!unlocked) {
        ProLockedView(
            title = "多设备离线共享",
            desc = "两台手机同步是 Pro 专属功能",
            onUpgrade = onUpgrade
        )
        return
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val syncManager = remember { SyncManager() }
    val network = remember {
        SyncNetwork { remoteJson ->
            android.util.Log.d("Sync", "onBundleReceived, remote=${remoteJson.length}")
            val remote = SyncMerger.bundleFromJson(remoteJson)
            val merged = SyncMerger.mergeChanges(syncManager.extractChanges(), remote)
            android.util.Log.d("Sync", "merged=${merged.size}")
            syncManager.applyChanges(merged)
            SyncMerger.bundleToJson(merged)
        }
    }
    var status by remember { mutableStateOf("未启动") }
    var port by remember { mutableStateOf<Int?>(null) }
    var targetIp by remember { mutableStateOf("") }
    var targetPort by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                result = runCatching {
                    val json = SyncMerger.bundleToJson(syncManager.extractChanges())
                    context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray(Charsets.UTF_8)) }
                    "同步文件已导出"
                }.getOrElse { "导出失败：${it.message}" }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                result = runCatching {
                    val text = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        ?.toString(Charsets.UTF_8) ?: error("读取失败")
                    val remote = SyncMerger.bundleFromJson(text)
                    val merged = SyncMerger.mergeChanges(syncManager.extractChanges(), remote)
                    syncManager.applyChanges(merged)
                    "导入合并完成：${merged.size} 条数据"
                }.getOrElse { "导入失败：${it.message}" }
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
        OverlayHeader("共享同步", onBack)
        Text(
            "两台手机连同一个 Wi-Fi 即可离线同步；无网络时可用文件同步。",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        SyncAction(
            title = "开始接收（等待对方连接）",
            desc = "本机作为接收方，显示端口后让另一台连接",
            onClick = {
                scope.launch {
                    result = runCatching {
                        val p = network.start()
                        port = p
                        status = "正在监听端口 $p…"
                        scope.launch { network.serve() }
                        "接收服务已启动"
                    }.getOrElse { "启动失败：${it.message}" }
                }
                Unit
            }
        )
        if (port != null) {
            Text(
                "本机端口：$port",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Mint,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Text(
            "连接对方设备",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = targetIp,
                onValueChange = { targetIp = it },
                placeholder = { Text("对方 IP", fontSize = 13.sp, color = TextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Card,
                    unfocusedContainerColor = Card,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = targetPort,
                onValueChange = { targetPort = it },
                placeholder = { Text("端口", fontSize = 13.sp, color = TextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Card,
                    unfocusedContainerColor = Card,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            Text(
                text = "同步",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Mint,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        android.util.Log.d("Sync", "sync button clicked, ip=${targetIp}, port=${targetPort}")
                        scope.launch {
                            result = runCatching {
                                val portNum = targetPort.toIntOrNull() ?: error("端口格式错误")
                                val local = SyncMerger.bundleToJson(syncManager.extractChanges())
                                android.util.Log.d("Sync", "sending bundle size=${local.length} to $targetIp:$portNum")
                                val mergedJson = SyncNetwork.sendBundle(targetIp.trim(), portNum, local)
                                val merged = SyncMerger.bundleFromJson(mergedJson)
                                syncManager.applyChanges(merged)
                                "同步完成：双方 ${merged.size} 条数据一致"
                            }.getOrElse { "同步失败：${it.message}" }
                        }
                    }
            )
        }

        Text(
            "文件同步（无网络兜底）",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            SyncAction("导出同步文件", "把本机数据存成文件发给对方", {
                exportLauncher.launch("baby-sync-${System.currentTimeMillis()}.json")
                Unit
            }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            SyncAction("导入合并", "选择对方的同步文件合并进来", {
                importLauncher.launch(arrayOf("*/*"))
                Unit
            }, modifier = Modifier.weight(1f))
        }

        result?.let {
            Text(
                it,
                fontSize = 13.sp,
                color = if (it.startsWith("同步完成") || it.startsWith("导入") || it.startsWith("接收") || it.contains("导出")) Mint else Color(0xFFD96A6A),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
        Text(
            "说明：Wi-Fi 传输为骨架实现（裸 Socket + 变更集合并），蓝牙与照片文件同步待真机验证完善。",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SyncAction(
    title: String,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
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
