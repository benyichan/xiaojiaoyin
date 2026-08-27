package com.xiaojiaoyin.baby.widget

import android.content.Context
import android.graphics.Bitmap
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.PhotoStorage
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.domain.AnniversaryCalc
import com.xiaojiaoyin.baby.domain.BabyAge

/** 小组件数据加载：当前宝宝 + 月龄 + 生日倒计时 + 今日待办数 */
data class WidgetBabyData(
    val baby: BabyEntity?,
    val ageText: String,
    val birthdayCountdown: String?,
    val todayTodoCount: Int,
    val avatarBitmap: Bitmap?
)

object WidgetData {

    suspend fun load(context: Context): WidgetBabyData {
        if (!AppGraph.isInitialized) {
            AppGraph.init(context)
        }
        val babyRepo = AppGraph.babyRepository
        val babies = babyRepo.getAll()
        val currentId = AppGraph.settingsRepository.getCurrentBabyId()
        val baby = babies.firstOrNull { it.id == currentId } ?: babies.firstOrNull()
        if (baby == null) {
            return WidgetBabyData(null, "", null, 0, null)
        }
        val now = System.currentTimeMillis()
        val daysToBirthday = AnniversaryCalc.daysUntil(baby.birthDateTime, repeatYearly = true, now)
        val birthdayText = when {
            daysToBirthday == 0L -> "今天生日"
            daysToBirthday > 0 -> "生日还有 $daysToBirthday 天"
            else -> null
        }
        val dayStart = java.time.Instant.ofEpochMilli(now).atZone(java.time.ZoneId.of("Asia/Shanghai"))
            .toLocalDate().atStartOfDay(java.time.ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli()
        val dayEnd = dayStart + 24L * 60 * 60 * 1000
        val todos = AppGraph.database.todoDao().getAllAll()
            .filter { it.babyId == baby.id && !it.completed && it.timeAt in dayStart until dayEnd }
        val avatar = if (baby.avatarPath.isNotBlank()) {
            runCatching {
                PhotoStorage.decodeThumb(PhotoStorage.loadFile(context, baby.avatarPath), 256)
            }.getOrNull()
        } else null
        return WidgetBabyData(
            baby = baby,
            ageText = BabyAge.text(baby.birthDateTime, now),
            birthdayCountdown = birthdayText,
            todayTodoCount = todos.size,
            avatarBitmap = avatar
        )
    }
}
