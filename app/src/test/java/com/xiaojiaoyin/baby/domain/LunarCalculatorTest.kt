package com.xiaojiaoyin.baby.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class LunarCalculatorTest {

    private fun millis(dateTime: String): Long =
        LocalDateTime.parse(dateTime)
            .atZone(ZoneId.of("Asia/Shanghai"))
            .toInstant()
            .toEpochMilli()

    @Test
    fun `2026-05-03 09-24 金牛座属马农历三月十七八字丙午年起`() {
        val info = LunarCalculator.computeDerivedInfo(
            birthDateTimeMillis = millis("2026-05-03T09:24"),
            todayMillis = millis("2026-08-15T00:00")
        )
        assertEquals("金牛座", info.constellation)
        assertEquals("马", info.zodiac)
        assertEquals("三月十七", info.lunarDate)
        assertTrue("八字应以丙午年起，实际：${info.baZi}", info.baZi.startsWith("丙午年"))
    }

    @Test
    fun `阳历生日倒计时_生日已过则算到明年`() {
        val info = LunarCalculator.computeDerivedInfo(
            birthDateTimeMillis = millis("2026-05-03T09:24"),
            todayMillis = millis("2026-08-15T00:00")
        )
        // 2026-08-15 到 2027-05-03 = 261 天
        assertEquals(261L, info.nextSolarBirthday.days)
        assertEquals("2027-05-03", info.nextSolarBirthday.dateText)
    }

    @Test
    fun `农历生日倒计时_跨年计算`() {
        val info = LunarCalculator.computeDerivedInfo(
            birthDateTimeMillis = millis("2026-05-03T09:24"),
            todayMillis = millis("2027-04-01T00:00")
        )
        // 出生农历三月十七，2027 年农历三月十七在 2027-04-22 前后（按库计算），必须晚于今天且为正
        assertTrue(info.nextLunarBirthday.days > 0)
        assertEquals("三月十七", info.nextLunarBirthday.dateText)
    }

    @Test
    fun `腊月出生_农历生日跨年到正月之后`() {
        // 2026-01-20 前后为腊月（农历乙巳年腊月初一附近）；用固定公历验证不崩溃且天数合理
        val info = LunarCalculator.computeDerivedInfo(
            birthDateTimeMillis = millis("2026-01-15T08:00"),
            todayMillis = millis("2026-02-10T00:00")
        )
        assertTrue(info.nextLunarBirthday.days in 1..400)
        assertTrue(info.nextSolarBirthday.days in 1..400)
    }
}
