package com.xiaojiaoyin.baby.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.domain.VaccineDose
import com.xiaojiaoyin.baby.domain.VaccineSchedule

/**
 * 疫苗接种提醒调度：对每个「未接种且建议日在未来」的剂次设一次闹钟（建议日 09:00）。
 * 建议日已过未种的不再设（避免重复骚扰，列表页有逾期状态兜底）。
 * 开机与 App 启动时全量重建；标记已种/宝宝删除时单宝宝重建。
 */
class VaccineScheduler(private val context: Context) {

    companion object {
        const val EXTRA_DOSE_KEY = "vaccine_dose_key"
        const val EXTRA_DOSE_TEXT = "vaccine_dose_text"
        const val EXTRA_BABY_NAME = "vaccine_baby_name"
        const val EXTRA_REQUEST_CODE = "vaccine_request_code"

        /** 请求码基数：待办低位、生日 1_000_000，疫苗 2_000_000 */
        private const val REQUEST_BASE = 2_000_000
        private const val SLOTS_PER_BABY = 32
    }

    suspend fun scheduleAll() {
        AppGraph.babyRepository.getAll().forEach { scheduleForBaby(it) }
    }

    suspend fun scheduleForBaby(baby: BabyEntity) {
        cancelForBaby(baby.id)
        val doses = VaccineSchedule.load(context)
        val now = System.currentTimeMillis()
        doses.forEachIndexed { index, dose ->
            val vaccinated = AppGraph.vaccinationRepository.getByDoseKey(baby.id, dose.key)
            if (vaccinated != null) return@forEachIndexed
            val due = VaccineSchedule.dueDate(baby.birthDateTime, dose)
            if (due <= now) return@forEachIndexed // 已过期未种不设，避免重复骚扰
            scheduleAlarm(baby, dose, due, index)
        }
    }

    fun cancelForBaby(babyId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (slot in 0 until SLOTS_PER_BABY) {
            val requestCode = requestCodeForSlot(babyId, slot)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                Intent(context, VaccineReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }
    }

    private fun scheduleAlarm(baby: BabyEntity, dose: VaccineDose, atMillis: Long, index: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCode = requestCodeForSlot(baby.id, index % SLOTS_PER_BABY)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, VaccineReceiver::class.java)
                .putExtra(EXTRA_DOSE_KEY, dose.key)
                .putExtra(EXTRA_DOSE_TEXT, "${dose.vaccine} ${dose.doseLabel}")
                .putExtra(EXTRA_BABY_NAME, baby.name)
                .putExtra(EXTRA_REQUEST_CODE, requestCode),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val canExact = Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()
        try {
            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, atMillis, pendingIntent)
            }
        } catch (t: Throwable) {
            android.util.Log.e("VaccineScheduler", "schedule failed", t)
            runCatching { alarmManager.set(AlarmManager.RTC_WAKEUP, atMillis, pendingIntent) }
        }
    }

    private fun requestCodeForSlot(babyId: Long, slot: Int): Int =
        REQUEST_BASE + (babyId % 60_000L).toInt() * SLOTS_PER_BABY + slot
}
