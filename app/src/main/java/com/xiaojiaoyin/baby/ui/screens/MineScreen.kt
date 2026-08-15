package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary

@Composable
fun MineScreen(
    onOpenTodo: () -> Unit = {},
    onOpenBackup: () -> Unit = {},
    onOpenMedical: () -> Unit = {},
    onOpenFeedingZone: () -> Unit = {},
    onOpenCryingZone: () -> Unit = {},
    onOpenSchool: () -> Unit = {},
    onOpenGoods: () -> Unit = {},
    onOpenAnniv: () -> Unit = {},
    onOpenSync: () -> Unit = {},
    onOpenPurchase: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 70.dp)
    ) {
        Text(
            text = "我的",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
        )
        MenuRow("宝宝管理") {}
        MenuRow("待办清单", onClick = onOpenTodo)
        MenuRow("医疗记录", onClick = onOpenMedical)
        MenuRow("喂养专区", onClick = onOpenFeedingZone)
        MenuRow("哭闹专区", onClick = onOpenCryingZone)
        MenuRow("学籍信息", onClick = onOpenSchool)
        MenuRow("好物清单", onClick = onOpenGoods)
        MenuRow("那年今日", onClick = onOpenAnniv)
        MenuRow("备份与恢复", onClick = onOpenBackup)
        MenuRow("共享同步", onClick = onOpenSync)
        MenuRow("提醒设置") {}
        MenuRow("订阅升级", onClick = onOpenPurchase)
        MenuRow("隐私政策") {}
        MenuRow("关于") {}
    }
}

@Composable
private fun MenuRow(label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .background(Card, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text("›", fontSize = 15.sp, color = TextSecondary)
    }
}
