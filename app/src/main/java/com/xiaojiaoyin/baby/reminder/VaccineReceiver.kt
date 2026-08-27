package com.xiaojiaoyin.baby.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** 疫苗接种到龄提醒：弹通知。一次性闹钟，触发即完，无需标记防重复 */
class VaccineReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val requestCode = intent.getIntExtra(VaccineScheduler.EXTRA_REQUEST_CODE, 0)
        val babyName = intent.getStringExtra(VaccineScheduler.EXTRA_BABY_NAME) ?: "宝宝"
        val doseText = intent.getStringExtra(VaccineScheduler.EXTRA_DOSE_TEXT) ?: "疫苗"
        val doseKey = intent.getStringExtra(VaccineScheduler.EXTRA_DOSE_KEY) ?: ""
        android.util.Log.d("Vaccine", "onReceive baby=$babyName dose=$doseKey")
        ReminderNotification.showVaccine(context, requestCode, babyName, doseText)
    }
}
