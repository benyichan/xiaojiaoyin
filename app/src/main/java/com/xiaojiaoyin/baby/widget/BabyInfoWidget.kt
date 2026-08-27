package com.xiaojiaoyin.baby.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.xiaojiaoyin.baby.MainActivity

/** 4x2 宝宝信息卡：左头像右信息（月龄 / 生日倒计时 / 今日待办） */
class BabyInfoWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BabyInfoWidget
}

object BabyInfoWidget : GlanceAppWidget() {

    private val Green = ColorProvider(Color(0xFF2E9E76))
    private val GreenDark = ColorProvider(Color(0xFF2F463D))
    private val White = ColorProvider(Color.White)
    private val PaleGreen = ColorProvider(Color(0xFFDFF5EA))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetData.load(context)
        provideContent {
            Content(data)
        }
    }

    @Composable
    private fun Content(data: WidgetBabyData) {
        val baby = data.baby
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Green)
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (baby == null) {
                Text(
                    "小脚印\n点按添加宝宝",
                    style = TextStyle(color = White, fontSize = 14.sp),
                    modifier = GlanceModifier.padding(start = 6.dp)
                )
                return@Row
            }
            if (data.avatarBitmap != null) {
                Image(
                    provider = ImageProvider(data.avatarBitmap),
                    contentDescription = baby.name,
                    contentScale = ContentScale.Crop,
                    modifier = GlanceModifier.size(56.dp).background(White)
                )
            } else {
                Box(
                    modifier = GlanceModifier.size(56.dp).background(White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        baby.name.take(1),
                        style = TextStyle(color = Green, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
            Column(modifier = GlanceModifier.padding(start = 14.dp)) {
                Text(
                    baby.name,
                    style = TextStyle(color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    data.ageText,
                    style = TextStyle(color = PaleGreen, fontSize = 12.sp),
                    modifier = GlanceModifier.padding(top = 3.dp)
                )
                Row(modifier = GlanceModifier.padding(top = 5.dp)) {
                    val infos = buildList {
                        data.birthdayCountdown?.let { add(it) }
                        if (data.todayTodoCount > 0) add("今日待办 ${data.todayTodoCount}")
                    }
                    Text(
                        if (infos.isEmpty()) "今天没有安排" else infos.joinToString(" · "),
                        style = TextStyle(color = PaleGreen, fontSize = 11.sp)
                    )
                }
            }
        }
    }
}
