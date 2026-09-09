package com.xiaojiaoyin.baby.domain

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** 宝宝月龄/天数文案（首页与桌面小组件共用） */
object BabyAge {

    /** 「第 X 天 · X个月」/「第 X 天 · X岁X个月」；出生当天 = 第 1 天 */
    fun text(birthMillis: Long, now: Long): String {
        // 按自然日数天数：直接用时间戳整除会在出生次日同刻才翻到第 2 天（如 18 点出生，次日 18 点前一直显示第 1 天）
        val zone = ZoneId.of("Asia/Shanghai")
        val birthDate = Instant.ofEpochMilli(birthMillis).atZone(zone).toLocalDate()
        val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
        val days = ChronoUnit.DAYS.between(birthDate, today).toInt() + 1
        val months = FeedRules.ageMonths(birthMillis, now)
        val age = when {
            months < 1 -> "出生不到 1 个月"
            months < 24 -> {
                val years = months / 12
                val rest = months % 12
                if (years == 0) "${months}个月" else "${years}岁${rest}个月"
            }
            else -> "${months / 12}岁"
        }
        return "第 $days 天 · $age"
    }
}
