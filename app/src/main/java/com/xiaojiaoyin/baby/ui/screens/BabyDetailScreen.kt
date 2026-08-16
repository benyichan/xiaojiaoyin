package com.xiaojiaoyin.baby.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.BabyCustomFieldEntity
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val FIELD_TEMPLATES = listOf("身份证号", "血型", "过敏史", "民族", "疫苗接种机构", "出生医院", "身高出生值", "体重出生值")

@Composable
fun BabyDetailScreen(babyId: Long, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var baby by remember { mutableStateOf<com.xiaojiaoyin.baby.data.db.entity.BabyEntity?>(null) }
    val fields by remember(babyId) {
        AppGraph.babyCustomFieldRepository.observeAll(babyId)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    var editing by remember { mutableStateOf<BabyCustomFieldEntity?>(null) }
    var showAdd by remember { mutableStateOf(false) }
    var copiedKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(babyId) {
        baby = AppGraph.babyRepository.getById(babyId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        OverlayHeader("宝宝详情", onBack)

        baby?.let { b ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(Card, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(MintLight, RoundedCornerShape(99.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(b.name.take(1), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Mint)
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text("${b.name} · ${b.gender}宝", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        "小名 ${b.nickname.ifBlank { "未填" }} · ${formatBabyDate(b.birthDateTime)}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "自定义信息",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                "＋ 添加",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Mint,
                modifier = Modifier.clickable { showAdd = true }
            )
        }

        Text(
            "常用模板（点击添加）",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            FIELD_TEMPLATES.forEach { tpl ->
                Text(
                    tpl,
                    fontSize = 11.sp,
                    color = Mint,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .background(MintLight, RoundedCornerShape(99.dp))
                        .clickable {
                            scope.launch {
                                AppGraph.babyCustomFieldRepository.add(babyId, tpl, "")
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        if (fields.isEmpty()) {
            Text(
                "还没有自定义信息，点模板或右上角添加，如血型、身份证号等",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 30.dp)
            ) {
                items(fields, key = { it.id }) { field ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Card, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(field.fieldKey, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                field.fieldValue.ifBlank { "未填写" },
                                fontSize = 12.sp,
                                color = if (field.fieldValue.isBlank()) TextSecondary else TextPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Text(
                            if (copiedKey == field.fieldKey) "已复制" else "复制",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Mint,
                            modifier = Modifier.clickable {
                                if (field.fieldValue.isNotBlank()) {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText(field.fieldKey, field.fieldValue))
                                    copiedKey = field.fieldKey
                                }
                            }
                        )
                        Text(
                            "编辑",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Mint,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable { editing = field }
                        )
                        Text(
                            "删",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD96A6A),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable {
                                    scope.launch { AppGraph.babyCustomFieldRepository.delete(field) }
                                }
                        )
                    }
                }
            }
        }
    }

    if (showAdd || editing != null) {
        CustomFieldDialog(
            initial = editing,
            onDismiss = { showAdd = false; editing = null },
            onSave = { key, value ->
                scope.launch {
                    if (editing != null) {
                        AppGraph.babyCustomFieldRepository.update(editing!!.copy(fieldKey = key, fieldValue = value))
                    } else {
                        AppGraph.babyCustomFieldRepository.add(babyId, key, value)
                    }
                }
                showAdd = false
                editing = null
            }
        )
    }
}

@Composable
private fun CustomFieldDialog(
    initial: BabyCustomFieldEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var key by remember(initial) { mutableStateOf(initial?.fieldKey ?: "") }
    var value by remember(initial) { mutableStateOf(initial?.fieldValue ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "添加自定义信息" else "编辑", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(
                    value = key,
                    onValueChange = { key = it },
                    placeholder = { Text("字段名，如 血型", fontSize = 12.sp) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5FBF8),
                        unfocusedContainerColor = Color(0xFFF5FBF8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = value,
                    onValueChange = { value = it },
                    placeholder = { Text("内容，如 A 型", fontSize = 12.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5FBF8),
                        unfocusedContainerColor = Color(0xFFF5FBF8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (key.isNotBlank()) onSave(key.trim(), value.trim())
            }) { Text("保存", color = Mint) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

private fun formatBabyDate(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
