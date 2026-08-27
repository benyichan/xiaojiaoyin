package com.xiaojiaoyin.baby.domain

/** 宝宝月龄/天数文案（首页与桌面小组件共用） */
object BabyAge {

    /** 「第 X 天 · X个月」/「第 X 天 · X岁X个月」；出生当天 = 第 1 天 */
    fun text(birthMillis: Long, now: Long): String {
        val days = ((now - birthMillis) / FeedRules.DAY_MS).toInt() + 1
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
