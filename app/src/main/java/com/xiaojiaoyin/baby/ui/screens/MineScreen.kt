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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.data.settings.ProStatusRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.navigationBarsPadding

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
    onOpenAnniversary: () -> Unit = {},
    onOpenVaccine: () -> Unit = {},
    onOpenSync: () -> Unit = {},
    onOpenPurchase: () -> Unit = {},
    onOpenBabyManage: () -> Unit = {},
    onOpenReminderSettings: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {},
    onOpenAbout: () -> Unit = {}
) {
    val isPro by AppGraph.proStatusRepository.isPro.collectAsStateWithLifecycle(initialValue = false)
    val expireAt by AppGraph.proStatusRepository.proExpireAt.collectAsStateWithLifecycle(initialValue = 0L)
    val trialStartAt by AppGraph.proStatusRepository.trialStartAt.collectAsStateWithLifecycle(initialValue = 0L)
    val trialRemaining = if (trialStartAt > 0) {
        // 向上取整：试用期内首日显示「剩余 7 天」
        val remainingMs = trialStartAt + ProStatusRepository.TRIAL_DAYS * ProStatusRepository.DAY_MS - System.currentTimeMillis()
        if (remainingMs <= 0) 0L else (remainingMs + ProStatusRepository.DAY_MS - 1) / ProStatusRepository.DAY_MS
    } else 0L

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding().padding(bottom = 70.dp)
    ) {
        Text(
            text = "我的",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
        )
        MembershipCard(
            isPro = isPro,
            expireAt = expireAt,
            trialRemaining = trialRemaining,
            onUpgrade = onOpenPurchase
        )
        MenuRow("宝宝管理", onClick = onOpenBabyManage)
        MenuRow("待办清单", onClick = onOpenTodo)
        MenuRow("医疗记录", onClick = onOpenMedical)
        MenuRow("疫苗接种", onClick = onOpenVaccine)
        MenuRow("喂养专区", onClick = onOpenFeedingZone)
        MenuRow("哭闹专区", onClick = onOpenCryingZone)
        MenuRow("学籍信息", onClick = onOpenSchool)
        MenuRow("好物清单", onClick = onOpenGoods)
        MenuRow("那年今日", onClick = onOpenAnniv)
        MenuRow("纪念日", onClick = onOpenAnniversary)
        MenuRow("备份与恢复", onClick = onOpenBackup)
        MenuRow("共享同步", onClick = onOpenSync)
        MenuRow("提醒设置", onClick = onOpenReminderSettings)
        MenuRow("订阅升级", onClick = onOpenPurchase)
        MenuRow("隐私政策", onClick = onOpenPrivacy)
        MenuRow("关于", onClick = onOpenAbout)
    }
}

@Composable
private fun MembershipCard(
    isPro: Boolean,
    expireAt: Long,
    trialRemaining: Long,
    onUpgrade: () -> Unit
) {
    if (isPro) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(Gold, RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Pro 会员",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                if (expireAt == 0L) "永久" else "有效期至 ${formatMemberExpire(expireAt)}",
                fontSize = 12.sp,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    } else if (trialRemaining > 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(Mint, RoundedCornerShape(18.dp))
                .clickable(onClick = onUpgrade)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Pro 试用中",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                "剩余 $trialRemaining 天",
                fontSize = 12.sp,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(Card, RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("免费版", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Text(
                    "升级解锁多宝宝、生长曲线、统计等",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Text(
                "升级 Pro",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier
                    .background(Gold, RoundedCornerShape(99.dp))
                    .clickable(onClick = onUpgrade)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

private fun formatMemberExpire(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
