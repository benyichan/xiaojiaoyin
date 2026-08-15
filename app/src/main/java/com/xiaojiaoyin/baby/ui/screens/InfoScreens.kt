package com.xiaojiaoyin.baby.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.reminder.ReminderPermissionHelper
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("关于", onBack)
        InfoBlock("小脚印", "宝宝成长记录 · 完全离线")
        InfoBlock("版本", "v0.1.0")
        InfoBlock("定位", "一款完全离线的本地宝宝成长记录 App：所有数据仅存于你的设备，可备份、可局域网共享，不经过任何服务器。")
        InfoBlock("联系方式", "benyi@aliyun.com")
    }
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("隐私政策", onBack)
        InfoBlock("数据处理", "小脚印完全离线运行：宝宝档案、照片、记录、待办等全部数据仅存储在你自己的设备上，不上传、不传输到任何服务器。")
        InfoBlock("儿童信息", "记录的是儿童成长信息，处理者即家长本人；应用不展示广告、不采集儿童行为数据，家长可随时导出或删除全部数据。")
        InfoBlock("权限", "通知与精确闹钟：用于待办到点提醒；相册：仅在你主动选择照片时访问；蓝牙：用于设备间同步；电池优化白名单需你手动开启以保证提醒可靠。")
        InfoBlock("数据导出与删除", "设置 → 备份与恢复可导出完整备份；各专区提供删除入口；卸载应用即清除全部本地数据。")
    }
}

@Composable
fun ReminderSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("提醒设置", onBack)
        Text(
            "为了让待办提醒准点送达，请确保以下权限已开启：",
            fontSize = 13.sp,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        SettingsAction("通知权限", "Android 13+ 需要授权才能收到提醒") {
            ReminderPermissionHelper.requestNotificationPermission(context as? androidx.activity.ComponentActivity ?: return@SettingsAction)
        }
        SettingsAction("精确闹钟", "系统默认关闭，需要手动开启") {
            if (!ReminderPermissionHelper.canScheduleExact(context)) {
                ReminderPermissionHelper.openExactAlarmSettings(context)
            }
        }
        SettingsAction("电池优化白名单", "防止国产 ROM 后台杀进程导致提醒失效") {
            if (!ReminderPermissionHelper.isIgnoringBatteryOptimizations(context)) {
                ReminderPermissionHelper.openBatterySettings(context)
            }
        }
        ReminderPermissionHelper.vendorHint(context)?.let {
            InfoBlock("本机提示", it)
        }
        InfoBlock("重启恢复", "手机重启后，所有已开启的提醒会自动恢复，无需重复设置。")
    }
}

@Composable
private fun SettingsAction(title: String, desc: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .background(Card, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(desc, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 3.dp))
        Text("去设置 ›", fontSize = 12.sp, color = Mint, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun InfoBlock(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .background(Card, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(content, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 6.dp))
    }
}
