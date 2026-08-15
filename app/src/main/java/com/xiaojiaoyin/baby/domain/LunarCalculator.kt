package com.xiaojiaoyin.baby.domain

import com.nlf.calendar.Lunar
import com.nlf.calendar.Solar
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs

data class BirthdayCountdown(
    val days: Long,
    val dateText: String
)

data class DerivedInfo(
    val lunarDate: String,
    val zodiac: String,
    val constellation: String,
    val baZi: String,
    val nextSolarBirthday: BirthdayCountdown,
    val nextLunarBirthday: BirthdayCountdown
)

object LunarCalculator {
    private const val DAY_MS = 24L * 60 * 60 * 1000
    private val zone: ZoneId = ZoneId.of("Asia/Shanghai")

    fun computeDerivedInfo(birthDateTimeMillis: Long, todayMillis: Long = System.currentTimeMillis()): DerivedInfo {
        val birth = Solar.fromDate(java.util.Date(birthDateTimeMillis))
        val birthLunar = birth.lunar

        val solarBirthday = nextSolarBirthday(birthDateTimeMillis, todayMillis)
        val lunarBirthday = nextLunarBirthday(birthLunar, todayMillis)

        return DerivedInfo(
            lunarDate = "${birthLunar.monthInChinese}月${birthLunar.dayInChinese}",
            zodiac = birthLunar.shengxiao,
            constellation = birth.xingZuo.let { if (it.endsWith("座")) it else "${it}座" },
            baZi = "${birthLunar.yearInGanZhi}年 · ${birthLunar.monthInGanZhi}月 · ${birthLunar.dayInGanZhi}日 · ${birthLunar.timeInGanZhi}时",
            nextSolarBirthday = solarBirthday,
            nextLunarBirthday = lunarBirthday
        )
    }

    private fun nextSolarBirthday(birthDateTimeMillis: Long, todayMillis: Long): BirthdayCountdown {
        val birth = ZonedDateTime.ofInstant(Instant.ofEpochMilli(birthDateTimeMillis), zone)
        val today = ZonedDateTime.ofInstant(Instant.ofEpochMilli(todayMillis), zone)
        val birthMonthDay = "${birth.monthValue.toString().padStart(2, '0')}-${birth.dayOfMonth.toString().padStart(2, '0')}"

        var target = today
            .withMonth(birth.monthValue)
            .withDayOfMonth(birth.dayOfMonth)
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
        if (target.toEpochSecond() < today.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochSecond()) {
            target = target.plusYears(1)
        }
        val days = (target.toEpochSecond() - today.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochSecond()) / 86400L
        return BirthdayCountdown(days = days, dateText = "${target.year}-$birthMonthDay")
    }

    private fun nextLunarBirthday(birthLunar: Lunar, todayMillis: Long): BirthdayCountdown {
        val todaySolar = Solar.fromDate(java.util.Date(todayMillis))
        val todayLunar = todaySolar.lunar
        val birthMonth = abs(birthLunar.month)
        val birthDay = birthLunar.day
        val todayDate = Instant.ofEpochMilli(todayMillis).atZone(zone).toLocalDate()

        fun lunarSolarMillis(year: Int): Long? = runCatching {
            val solar = Lunar(year, birthMonth, birthDay).solar
            LocalDate.of(solar.year, solar.month, solar.day)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()
        }.getOrNull()

        val todayStart = todayDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val thisYearMillis = lunarSolarMillis(todayLunar.year)
        val nextYearMillis = lunarSolarMillis(todayLunar.year + 1)

        val candidateMillis = when {
            thisYearMillis != null && thisYearMillis >= todayStart -> thisYearMillis
            nextYearMillis != null -> nextYearMillis
            else -> {
                todayDate.plusYears(1).atStartOfDay(zone).toInstant().toEpochMilli()
            }
        }

        val days = (candidateMillis - todayStart) / DAY_MS
        return BirthdayCountdown(
            days = days,
            dateText = "${birthLunar.monthInChinese}月${birthLunar.dayInChinese}"
        )
    }
}
