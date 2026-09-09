package com.xiaojiaoyin.baby.domain

import com.xiaojiaoyin.baby.data.db.entity.RecordType
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object FeedRules {
    const val DAY_MS = 24L * 60 * 60 * 1000
    const val AUTO_HIDE_GAP_DAYS = 60L

    /** 各类型活跃月龄上限；不在表中的类型永远活跃 */
    val ACTIVE_MONTHS: Map<RecordType, Int> = mapOf(
        RecordType.FEEDING to 24,
        RecordType.CRYING to 36
    )

    fun ageMonths(birthDateTimeMillis: Long, now: Long = System.currentTimeMillis()): Int {
        // 按真实日历月龄（到出生日的 day-of-month 才算满 N 月）；
        // 旧实现对月初截断，1 月 15 日出生的宝宝 2 月 1 日就显示「1个月」，满月最多提前近一个月
        val birth = Instant.ofEpochMilli(birthDateTimeMillis).atZone(ZoneId.of("Asia/Shanghai")).toLocalDate()
        val today = Instant.ofEpochMilli(now).atZone(ZoneId.of("Asia/Shanghai")).toLocalDate()
        return ChronoUnit.MONTHS.between(birth, today).coerceAtLeast(0).toInt()
    }

    fun isActive(type: RecordType, ageMonths: Int): Boolean =
        ACTIVE_MONTHS[type]?.let { ageMonths <= it } ?: true

    fun shouldAutoHide(
        type: RecordType,
        ageMonths: Int,
        lastRecordAt: Long?,
        now: Long = System.currentTimeMillis()
    ): Boolean {
        if (!isActive(type, ageMonths)) return true
        if (type !in ACTIVE_MONTHS) return false
        val last = lastRecordAt ?: return false
        return (now - last) > AUTO_HIDE_GAP_DAYS * DAY_MS
    }
}
