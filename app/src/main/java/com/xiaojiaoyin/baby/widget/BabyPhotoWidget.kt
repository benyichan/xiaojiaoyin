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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.xiaojiaoyin.baby.MainActivity
import com.xiaojiaoyin.baby.R

/** 2x2 宝宝照片卡：头像铺满 + 底部名字条；无头像时绿底名字首字 */
class BabyPhotoWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BabyPhotoWidget
}

object BabyPhotoWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetData.load(context)
        provideContent {
            Content(data)
        }
    }

    @Composable
    private fun Content(data: WidgetBabyData) {
        val baby = data.baby
        Box(
            modifier = GlanceModifier.fillMaxSize().clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            if (baby == null) {
                Box(
                    modifier = GlanceModifier.fillMaxSize().background(ColorProvider(Color(0xFF2E9E76))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "小脚印",
                        style = TextStyle(color = ColorProvider(Color.White), fontSize = 15.sp),
                        modifier = GlanceModifier.padding(8.dp)
                    )
                }
                return@Box
            }
            if (data.avatarBitmap != null) {
                Image(
                    provider = ImageProvider(data.avatarBitmap),
                    contentDescription = baby.name,
                    contentScale = ContentScale.Crop,
                    modifier = GlanceModifier.fillMaxSize()
                )
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(Color(0xB32F463D)))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        baby.name,
                        style = TextStyle(color = ColorProvider(Color.White), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        data.ageText,
                        style = TextStyle(color = ColorProvider(Color(0xFFDFF5EA)), fontSize = 11.sp)
                    )
                }
            } else {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ColorProvider(Color(0xFF2E9E76)))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier.background(ColorProvider(Color(0xFFE4F6EE))).padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            baby.name.take(1),
                            style = TextStyle(color = ColorProvider(Color(0xFF2E9E76)), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        baby.name,
                        style = TextStyle(color = ColorProvider(Color.White), fontSize = 15.sp, fontWeight = FontWeight.Bold),
                        modifier = GlanceModifier.padding(top = 6.dp)
                    )
                    Text(
                        data.ageText,
                        style = TextStyle(color = ColorProvider(Color(0xFFDFF5EA)), fontSize = 11.sp)
                    )
                }
            }
        }
    }
}
