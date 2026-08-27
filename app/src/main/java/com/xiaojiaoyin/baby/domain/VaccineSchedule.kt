package com.xiaojiaoyin.baby.domain

import android.content.Context
import com.xiaojiaoyin.baby.data.db.entity.VaccinationEntity
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class VaccineDose(
    val key: String,
    val vaccine: String,
    val doseLabel: String,
    val ageLabel: String,
    val ageMonths: Int
)

enum class VaccineStatus { DONE, DUE_SOON, OVERDUE, UPCOMING }

/** 疫苗日程：内置 assets/vaccine_schedule.json（2021 版国家免疫规划），纯计算供 UI 与提醒共用 */
object VaccineSchedule {

    private const val ZONE = "Asia/Shanghai"

    fun load(context: Context): List<VaccineDose> {
        val json = context.assets.open("vaccine_schedule.json").bufferedReader().use { it.readText() }
        val arr = JSONObject(json).getJSONArray("doses")
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            VaccineDose(
                key = o.getString("key"),
                vaccine = o.getString("vaccine"),
                doseLabel = o.getString("doseLabel"),
                ageLabel = o.getString("ageLabel"),
                ageMonths = o.getInt("ageMonths")
            )
        }
    }

    /** 建议接种日 = 出生日 + ageMonths 个月（当天 09:00，供提醒用） */
    fun dueDate(birthMillis: Long, dose: VaccineDose): Long {
        val zone = ZoneId.of(ZONE)
        val birth = Instant.ofEpochMilli(birthMillis).atZone(zone).toLocalDate()
        val due = birth.plusMonths(dose.ageMonths.toLong())
        return due.atTime(9, 0).atZone(zone).toInstant().toEpochMilli()
    }

    fun dueDateText(birthMillis: Long, dose: VaccineDose): String {
        val zone = ZoneId.of(ZONE)
        val birth = Instant.ofEpochMilli(birthMillis).atZone(zone).toLocalDate()
        val due = birth.plusMonths(dose.ageMonths.toLong())
        return due.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    /** 状态判定：已种 > 到龄未种（含过期）> 30 天内到期 > 未到龄 */
    fun statusOf(
        dose: VaccineDose,
        vaccinated: VaccinationEntity?,
        birthMillis: Long,
        now: Long
    ): VaccineStatus {
        if (vaccinated != null) return VaccineStatus.DONE
        val due = dueDate(birthMillis, dose)
        return when {
            now >= due -> VaccineStatus.OVERDUE
            due - now <= 30L * 24 * 60 * 60 * 1000 -> VaccineStatus.DUE_SOON
            else -> VaccineStatus.UPCOMING
        }
    }
}
