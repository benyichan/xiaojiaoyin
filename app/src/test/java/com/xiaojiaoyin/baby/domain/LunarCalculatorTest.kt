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

    @Test
    fun `阳历生日提醒时间_当天9点前算当天_9点后算明年`() {
        val birth = millis("2026-05-03T09:24")
        assertEquals(
            millis("2027-05-03T09:00"),
            LunarCalculator.nextSolarBirthdayTime(birth, millis("2026-08-15T10:00"), 9)
        )
        assertEquals(
            millis("2027-05-03T09:00"),
            LunarCalculator.nextSolarBirthdayTime(birth, millis("2027-05-03T08:00"), 9)
        )
    }

    @Test
    fun `2月29日出生_平年自动顺延到2月28`() {
        val birth = millis("2024-02-29T08:00")
        assertEquals(
            millis("2026-02-28T09:00"),
            LunarCalculator.nextSolarBirthdayTime(birth, millis("2026-02-27T10:00"), 9)
        )
    }

    @Test
    fun `农历生日提醒时间_晚于今天且落在次年`() {
        val birth = millis("2026-05-03T09:24")
        val now = millis("2026-08-15T10:00")
        val at = LunarCalculator.nextLunarBirthdayTime(birth, now, 9)
        assertTrue(at > now)
        assertTrue(at - now < 400L * 24 * 3600 * 1000)
        val date = java.time.Instant.ofEpochMilli(at).atZone(ZoneId.of("Asia/Shanghai"))
        assertEquals(2027, date.year)
    }

    @Test
    fun `2月29日出生_倒计时在平年不崩溃且指向2月28`() {
        val info = LunarCalculator.computeDerivedInfo(
            birthDateTimeMillis = millis("2024-02-29T08:00"),
            todayMillis = millis("2026-08-15T00:00")
        )
        assertEquals("2027-02-28", info.nextSolarBirthday.dateText)
        assertTrue(info.nextSolarBirthday.days > 0)
    }

    @Test
    fun `农历生日提醒时间_生日当天未到9点仍算今天_过了9点排明年`() {
        val birth = millis("2026-05-03T09:24")
        // 以很久之前的 now 求出下一个农历生日的 09:00 时刻
        val at = LunarCalculator.nextLunarBirthdayTime(birth, millis("2026-08-15T10:00"), 9)
        // 生日当天 08:00 再算，应仍得当天 09:00（旧实现在生日当天会跳过到明年）
        assertEquals(at, LunarCalculator.nextLunarBirthdayTime(birth, at - 3600_000L, 9))
        // 生日当天 10:00（已过 9 点）应排到明年
        val next = LunarCalculator.nextLunarBirthdayTime(birth, at + 3600_000L, 9)
        assertTrue(next > at)
    }

    @Test
    fun `闰月出生_农历生日不崩溃且天数合理`() {
        // 2025 年闰六月，公历 2025-07-25 为闰六月初一
        val info = LunarCalculator.computeDerivedInfo(
            birthDateTimeMillis = millis("2025-07-25T08:00"),
            todayMillis = millis("2026-08-15T00:00")
        )
        // 2026 年无闰六月，应回退到平六月，保证每年都能过
        assertTrue(info.nextLunarBirthday.days in 1..400)
        assertTrue(info.nextSolarBirthday.days in 1..400)
        // 提醒调度同样不崩溃
        val at = LunarCalculator.nextLunarBirthdayTime(millis("2025-07-25T08:00"), millis("2026-08-15T10:00"), 9)
        assertTrue(at > millis("2026-08-15T10:00"))
    }

    @Test
    fun `周岁年龄计算`() {
        val birth = millis("2026-05-12T09:15")
        assertEquals(0, LunarCalculator.ageInYears(birth, millis("2027-05-11T08:00")))
        assertEquals(1, LunarCalculator.ageInYears(birth, millis("2027-05-12T08:00")))
        assertEquals(2, LunarCalculator.ageInYears(birth, millis("2028-05-12T08:00")))
    }
}
