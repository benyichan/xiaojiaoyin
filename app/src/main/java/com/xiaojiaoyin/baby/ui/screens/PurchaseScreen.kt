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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun PurchaseScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val isPro by AppGraph.proStatusRepository.isPro.collectAsStateWithLifecycle(initialValue = false)
    var message by remember { mutableStateOf<String?>(null) }
    val activatePro: (String) -> Unit = { label ->
        scope.launch {
            AppGraph.proStatusRepository.setPro(true)
            message = "$label 已激活（演示）"
        }
    }

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
                    "已是 Pro 用户，感谢支持 🎉",
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

        PurchaseOption("月度订阅", "¥18 / 月", "随时取消") {
            activatePro("月度订阅（演示）")
        }
        PurchaseOption("年度订阅", "¥128 / 年", "约 9 折，最受欢迎") {
            activatePro("年度订阅（演示）")
        }
        PurchaseOption("永久买断", "¥298", "一次买断，终身使用") {
            activatePro("永久买断（演示）")
        }

        message?.let {
            Text(
                it,
                fontSize = 12.sp,
                color = Mint,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        Text(
            "当前为开发演示：点击即激活 Pro，不产生真实扣费。正式版将接入应用商店 IAP（Google Play Billing / 国内各商店 SDK），购买与恢复购买走商店回调。",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

}

@Composable
private fun PurchaseOption(title: String, price: String, desc: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .background(Card, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(desc, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
        Text(price, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Mint)
    }
}
