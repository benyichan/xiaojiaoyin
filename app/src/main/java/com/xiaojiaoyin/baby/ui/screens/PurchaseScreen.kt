package com.xiaojiaoyin.baby.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.R
import com.xiaojiaoyin.baby.domain.License
import com.xiaojiaoyin.baby.domain.LicenseManager
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val DEV_EMAIL = "benyi@aliyun.com"

@Composable
fun PurchaseScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val deviceId = remember { LicenseManager.deviceId(context) }
    val isPro by AppGraph.proStatusRepository.isPro.collectAsStateWithLifecycle(initialValue = false)
    val expireAt by AppGraph.proStatusRepository.proExpireAt.collectAsStateWithLifecycle(initialValue = 0L)
    var codeInput by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var copied by remember { mutableStateOf(false) }
    var selectedPlan by remember { mutableStateOf("L") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("订阅升级", onBack)

        if (isPro) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Gold, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = if (expireAt == 0L) "已是 Pro 用户（永久）" else "Pro 有效期至 ${formatExpire(expireAt)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Text(
            "Pro 专属权益",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        listOf("多宝宝档案", "完整生长曲线 + 数据看板", "无限照片空间", "多设备离线共享", "高级提醒（重复提醒）").forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text("✓", fontSize = 14.sp, color = Mint, fontWeight = FontWeight.Bold)
                Text(it, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Text(
            "价格",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            PriceCard(
                title = "月度",
                price = "¥6.8 / 月",
                desc = "随时停用",
                selected = selectedPlan == "M",
                modifier = Modifier.weight(1f)
            ) { selectedPlan = "M" }
            PriceCard(
                title = "永久",
                price = "¥159",
                desc = "一次买断",
                selected = selectedPlan == "L",
                modifier = Modifier.weight(1f)
            ) { selectedPlan = "L" }
        }

        Text(
            "购买流程",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        StepRow("1", "扫码付款：¥6.8（月）或 ¥159（永久）")
        StepRow("2", "发邮件到 benyi@aliyun.com，附上设备 ID 和你常用的邮箱")
        StepRow("3", "收到回复的激活码后粘贴到下面，点激活")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(Card, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Text("设备 ID（付款后随邮件发送）", fontSize = 12.sp, color = TextSecondary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    deviceId,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (copied) "已复制" else "复制",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Mint,
                    modifier = Modifier.clickable {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("deviceId", deviceId))
                        copied = true
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("开发者邮箱", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                Text(
                    DEV_EMAIL,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "复制",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Mint,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("devEmail", DEV_EMAIL))
                            copied = true
                        }
                )
            }
            Text(
                text = "复制申请内容（粘贴到邮件）",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(Mint, RoundedCornerShape(12.dp))
                    .clickable {
                        val planText = if (selectedPlan == "M") "月度 ¥6.8" else "永久 ¥159"
                        val content = "小脚印 Pro 激活申请\n设备 ID：$deviceId\n套餐：$planText\n回执邮箱：____（填你的邮箱）\n已付款，请回复激活码，谢谢！"
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("licenseApply", content))
                        copied = true
                    }
                    .padding(vertical = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(Card, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Text("激活码", fontSize = 12.sp, color = TextSecondary)
            TextField(
                value = codeInput,
                onValueChange = { codeInput = it.uppercase() },
                placeholder = { Text("XXXX-XXXX-XXXX-XXXX", fontSize = 13.sp, color = TextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5FBF8),
                    unfocusedContainerColor = Color(0xFFF5FBF8),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
            Text(
                text = "激活",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(Mint, RoundedCornerShape(14.dp))
                    .clickable {
                        scope.launch {
                            message = runCatching {
                                val info = License.verifyCode(
                                    codeInput.trim(),
                                    LicenseManager.deviceIdBytes(context)
                                ) ?: error("激活码无效，请检查是否复制完整、设备 ID 是否一致")
                                AppGraph.proStatusRepository.setProWithExpire(true, info.expireAt)
                                if (info.expireAt == 0L) "激活成功：永久 Pro" else "激活成功：Pro 至 ${formatExpire(info.expireAt)}"
                            }.getOrElse { "激活失败：${it.message}" }
                        }
                    }
                    .padding(vertical = 13.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        message?.let {
            Text(
                it,
                fontSize = 13.sp,
                color = if (it.startsWith("激活成功")) Mint else Color(0xFFD96A6A),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        Text(
            "付款收款码",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
        Image(
            painter = painterResource(R.drawable.payment_qr),
            contentDescription = "微信收款码",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(8.dp)
        )
        Text(
            "流程：扫码付款 → 把「复制申请内容」粘贴到邮件，发到 benyi@aliyun.com → 收到回复的激活码后输入激活。",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun PriceCard(
    title: String,
    price: String,
    desc: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .background(
                if (selected) com.xiaojiaoyin.baby.ui.theme.MintLight else Card,
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            title + if (selected) " ✓" else "",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Mint else TextSecondary
        )
        Text(price, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Mint, modifier = Modifier.padding(top = 4.dp))
        Text(desc, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun StepRow(index: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            index,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .background(Mint, RoundedCornerShape(99.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
        Text(
            text,
            fontSize = 13.sp,
            color = TextPrimary,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }
}

private fun formatExpire(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
