package com.xiaojiaoyin.baby.domain

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** 纪念日倒计时计算（纯函数，供 UI 与小组件共用） */
object AnniversaryCalc {

    /**
     * 距离下次纪念日的天数。
     * repeatYearly=true：按月/日算下一个周年（今天=0，已过今年则算明年）
     * repeatYearly=false：按绝对日期算剩余天数（负数 = 已过 N 天）
     */
    fun daysUntil(dateAt: Long, repeatYearly: Boolean, now: Long): Long {
        val zone = ZoneId.of("Asia/Shanghai")
        val date = Instant.ofEpochMilli(dateAt).atZone(zone).toLocalDate()
        val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
        if (!repeatYearly) return ChronoUnit.DAYS.between(today, date)
        var next = date.withYear(today.year)
        if (next.isBefore(today)) next = date.withYear(today.year + 1)
        return ChronoUnit.DAYS.between(today, next)
    }

    /** 倒计时描述：今天 / 明天 / N 天后 / 已过 N 天 */
    fun countdownText(dateAt: Long, repeatYearly: Boolean, now: Long): String {
        val d = daysUntil(dateAt, repeatYearly, now)
        return when {
            d == 0L -> "就是今天"
            d == 1L -> "明天"
            d > 0 -> "$d 天后"
            else -> "已过 ${-d} 天"
        }
    }
}
