package com.xiaojiaoyin.baby.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class BabyAgeTest {

    private fun millis(date: LocalDate, hour: Int, minute: Int): Long =
        date.atTime(hour, minute).atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli()

    @Test
    fun `出生当天算第1天`() {
        val birth = millis(LocalDate.of(2026, 1, 1), 18, 0)
        val now = millis(LocalDate.of(2026, 1, 1), 20, 0)
        assertTrue(BabyAge.text(birth, now).startsWith("第 1 天"))
    }

    @Test
    fun `次日按自然日翻到第2天_与出生时刻无关`() {
        // 回归：旧实现按时间戳整除，18 点出生的宝宝次日 10 点仍显示「第 1 天」
        val birth = millis(LocalDate.of(2026, 1, 1), 18, 0)
        val now = millis(LocalDate.of(2026, 1, 2), 10, 0)
        assertTrue(BabyAge.text(birth, now).startsWith("第 2 天"))
    }

    @Test
    fun `满月前不到一个月_满月当天算1个月`() {
        val birth = millis(LocalDate.of(2026, 1, 15), 9, 0)
        val beforeFullMonth = millis(LocalDate.of(2026, 2, 10), 9, 0)
        assertEquals("第 27 天 · 出生不到 1 个月", BabyAge.text(birth, beforeFullMonth))
        val fullMonth = millis(LocalDate.of(2026, 2, 15), 9, 0)
        assertEquals("第 32 天 · 1个月", BabyAge.text(birth, fullMonth))
    }
}
