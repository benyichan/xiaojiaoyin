package com.xiaojiaoyin.baby.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.domain.LunarCalculator
import java.time.Instant
import java.time.ZoneId

/**
 * 宝宝阳历/农历生日提醒调度。
 * 阳历、农历各自在生日当天 / 前一天 / 前三天早上 9 点提醒（每年共 6 条），
 * 触发后由 ReminderReceiver 自动排明年；开机与 App 启动时全量重建，宝宝增删改时单宝宝重建。
 */
class BirthdayScheduler(private val context: Context) {

    companion object {
        const val EXTRA_BABY_ID = "birthday_baby_id"
        const val EXTRA_BABY_NAME = "birthday_baby_name"
        const val EXTRA_BIRTHDAY_KIND = "birthday_kind"
        const val EXTRA_LEAD_DAYS = "birthday_lead_days"
        const val EXTRA_BIRTHDAY_TEXT = "birthday_text"
        const val EXTRA_REQUEST_CODE = "birthday_request_code"

        const val KIND_SOLAR = "solar"
        const val KIND_LUNAR = "lunar"

        /** 生日提醒默认时间：早上 9 点 */
        const val BIRTHDAY_HOUR = 9

        /** 提前提醒天数：3 天 / 1 天 / 当天 */
        val LEAD_OFFSETS = intArrayOf(3, 1, 0)

        private const val DAY_MS = 24L * 60 * 60 * 1000

        /** 请求码基数，与待办提醒（低位）错开；每宝宝 6 条（2 种历法 × 3 档提前量） */
        private const val REQUEST_BASE = 1_000_000
        private const val SLOTS_PER_BABY = 6
    }

    suspend fun scheduleAll() {
        AppGraph.babyRepository.getAll().forEach { scheduleForBaby(it) }
    }

    suspend fun scheduleForBaby(baby: BabyEntity) {
        val now = System.currentTimeMillis()
        scheduleKind(baby, KIND_SOLAR, now)
        scheduleKind(baby, KIND_LUNAR, now)
    }

    fun cancelForBaby(babyId: Long) {
        for (i in 0 until SLOTS_PER_BABY) {
            cancelAlarm(babyId, requestCodeForSlot(babyId, i))
        }
    }

    private suspend fun scheduleKind(baby: BabyEntity, kind: String, now: Long) {
        val base = when (kind) {
            KIND_SOLAR -> LunarCalculator.nextSolarBirthdayTime(baby.birthDateTime, now, BIRTHDAY_HOUR)
            else -> LunarCalculator.nextLunarBirthdayTime(baby.birthDateTime, now, BIRTHDAY_HOUR)
        }
        val dateText = when (kind) {
            KIND_SOLAR -> Instant.ofEpochMilli(base).atZone(ZoneId.of("Asia/Shanghai"))
                .let { "${it.monthValue}月${it.dayOfMonth}日" }
            else -> LunarCalculator.computeDerivedInfo(baby.birthDateTime, base).nextLunarBirthday.dateText
        }
        LEAD_OFFSETS.forEachIndexed { index, offset ->
            var atMillis = base - offset * DAY_MS
            if (atMillis <= now) {
                // 该档提醒已过，排到明年同一节点
                val nextBase = when (kind) {
                    KIND_SOLAR -> LunarCalculator.nextSolarBirthdayTime(
                        baby.birthDateTime, base + DAY_MS, BIRTHDAY_HOUR
                    )
                    else -> LunarCalculator.nextLunarBirthdayTime(
                        baby.birthDateTime, base + DAY_MS, BIRTHDAY_HOUR
                    )
                }
                atMillis = nextBase - offset * DAY_MS
            }
            scheduleAlarm(baby, kind, offset, dateText, atMillis, index)
        }
    }

    private fun scheduleAlarm(
        baby: BabyEntity,
        kind: String,
        leadDays: Int,
        dateText: String,
        atMillis: Long,
        leadIndex: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCode = requestCodeForSlot(baby.id, leadIndex + if (kind == KIND_SOLAR) 0 else 3)
        val pendingIntent = buildPendingIntent(baby, kind, leadDays, dateText, requestCode)
        val canExact = Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()
        android.util.Log.d(
            "BirthdayScheduler",
            "schedule baby=${baby.id} kind=$kind lead=$leadDays at=$atMillis canExact=$canExact rc=$requestCode"
        )
        try {
            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, pendingIntent)
            } else {
                // 未授权精确闹钟：Android 12+ 的 setAlarmClock 也受权限限制，降级为非精确闹钟
                alarmManager.set(AlarmManager.RTC_WAKEUP, atMillis, pendingIntent)
            }
        } catch (t: Throwable) {
            android.util.Log.e("BirthdayScheduler", "schedule failed, fallback to inexact", t)
            runCatching {
                alarmManager.set(AlarmManager.RTC_WAKEUP, atMillis, pendingIntent)
            }
        }
    }

    private fun cancelAlarm(babyId: Long, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

    private fun buildPendingIntent(
        baby: BabyEntity,
        kind: String,
        leadDays: Int,
        dateText: String,
        requestCode: Int
    ): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, ReminderReceiver::class.java)
                .putExtra(EXTRA_BABY_ID, baby.id)
                .putExtra(EXTRA_BABY_NAME, baby.name)
                .putExtra(EXTRA_BIRTHDAY_KIND, kind)
                .putExtra(EXTRA_LEAD_DAYS, leadDays)
                .putExtra(EXTRA_BIRTHDAY_TEXT, dateText)
                .putExtra(EXTRA_REQUEST_CODE, requestCode),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun requestCodeForSlot(babyId: Long, slot: Int): Int =
        REQUEST_BASE + (babyId % 100_000L).toInt() * SLOTS_PER_BABY + slot
}
