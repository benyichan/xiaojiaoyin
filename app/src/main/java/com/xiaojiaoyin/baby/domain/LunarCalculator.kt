package com.xiaojiaoyin.baby.domain

import com.nlf.calendar.Lunar
import com.nlf.calendar.Solar
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.YearMonth
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
        // 2/29 等日期在平年不存在：钳制到当月最后一天，与 nextSolarBirthdayTime 口径一致，
        // 否则 withDayOfMonth(29) 直接抛 DateTimeException
        fun solarCandidate(year: Int): ZonedDateTime {
            val maxDay = YearMonth.of(year, birth.monthValue).lengthOfMonth()
            val day = birth.dayOfMonth.coerceAtMost(maxDay)
            return ZonedDateTime.of(year, birth.monthValue, day, 0, 0, 0, 0, zone)
        }

        val todayStart = today.withHour(0).withMinute(0).withSecond(0).withNano(0)
        var target = solarCandidate(today.year)
        if (target.toEpochSecond() < todayStart.toEpochSecond()) {
            target = solarCandidate(today.year + 1)
        }
        val days = (target.toEpochSecond() - todayStart.toEpochSecond()) / 86400L
        // dateText 用钳制后的实际日期，避免平年显示"02-29"这种不存在的日期
        val dateText = "%04d-%02d-%02d".format(target.year, target.monthValue, target.dayOfMonth)
        return BirthdayCountdown(days = days, dateText = dateText)
    }

    private fun nextLunarBirthday(birthLunar: Lunar, todayMillis: Long): BirthdayCountdown {
        val todaySolar = Solar.fromDate(java.util.Date(todayMillis))
        val todayLunar = todaySolar.lunar
        // nlf 库用负数表示闰月（如闰六月 = -6）；保留符号，构造时优先按闰月取
        val birthMonth = birthLunar.month
        val birthDay = birthLunar.day
        val todayDate = Instant.ofEpochMilli(todayMillis).atZone(zone).toLocalDate()

        fun lunarSolarMillis(year: Int): Long? = runCatching {
            // 该年无此闰月时回退到同序数平月（闰月生日按平月过是民间惯例）
            val lunar = runCatching { Lunar(year, birthMonth, birthDay) }
                .getOrElse { Lunar(year, abs(birthMonth), birthDay) }
            val solar = lunar.solar
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

    /** 下一次阳历生日当天的指定时刻（epoch ms），用于生日提醒调度 */
    fun nextSolarBirthdayTime(birthDateTimeMillis: Long, nowMillis: Long, hour: Int): Long {
        val birth = ZonedDateTime.ofInstant(Instant.ofEpochMilli(birthDateTimeMillis), zone)
        val now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nowMillis), zone)

        fun candidateFor(year: Int): ZonedDateTime {
            val month = birth.monthValue
            val maxDay = YearMonth.of(year, month).lengthOfMonth()
            val day = birth.dayOfMonth.coerceAtMost(maxDay)
            return ZonedDateTime.of(year, month, day, hour, 0, 0, 0, zone)
        }

        var target = candidateFor(now.year)
        if (target.toEpochSecond() < now.toEpochSecond()) {
            target = candidateFor(now.year + 1)
        }
        return target.toInstant().toEpochMilli()
    }

    /** 下一次农历生日当天的指定时刻（epoch ms），用于生日提醒调度 */
    fun nextLunarBirthdayTime(birthDateTimeMillis: Long, nowMillis: Long, hour: Int): Long {
        val birthLunar = Solar.fromDate(java.util.Date(birthDateTimeMillis)).lunar
        // 负数表示闰月，保留符号
        val birthMonth = birthLunar.month
        val birthDay = birthLunar.day
        val todaySolar = Solar.fromDate(java.util.Date(nowMillis))
        val todayLunar = todaySolar.lunar
        val todayDate = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()

        fun lunarSolarStartMillis(year: Int): Long? = runCatching {
            // 该年无此闰月时回退到同序数平月
            val lunar = runCatching { Lunar(year, birthMonth, birthDay) }
                .getOrElse { Lunar(year, abs(birthMonth), birthDay) }
            val solar = lunar.solar
            LocalDate.of(solar.year, solar.month, solar.day)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()
        }.getOrNull()

        val thisYearMillis = lunarSolarStartMillis(todayLunar.year)
        val nextYearMillis = lunarSolarStartMillis(todayLunar.year + 1)
        val candidateDayStart = when {
            // 用"当天 hour 点"与 now 比较：生日当天且未到 hour 时仍算今年，
            // 避免整天 0 点时间戳与带时分秒的 now 比较导致生日当天被跳过
            thisYearMillis != null && thisYearMillis + hour * 3600_000L >= nowMillis -> thisYearMillis
            nextYearMillis != null -> nextYearMillis
            else -> todayDate.plusYears(1).atStartOfDay(zone).toInstant().toEpochMilli()
        }
        return candidateDayStart + hour * 3600_000L
    }

    /** 周岁年龄（不满 1 岁返回 0） */
    fun ageInYears(birthDateTimeMillis: Long, nowMillis: Long): Int {
        val birthDate = Instant.ofEpochMilli(birthDateTimeMillis).atZone(zone).toLocalDate()
        val nowDate = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        return Period.between(birthDate, nowDate).years
    }
}
