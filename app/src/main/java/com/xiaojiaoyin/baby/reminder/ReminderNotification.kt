package com.xiaojiaoyin.baby.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.xiaojiaoyin.baby.R

object ReminderNotification {
    const val CHANNEL_ID = "reminder"
    const val CHANNEL_ID_BIRTHDAY = "birthday"

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "待办提醒",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "待办事项到点提醒"
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    fun show(context: Context, todoId: Long, title: String) {
        ensureChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("小脚印提醒")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(todoId.toInt(), notification)
    }

    fun ensureBirthdayChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID_BIRTHDAY,
            "生日提醒",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "宝宝阳历/农历生日提醒"
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    const val CHANNEL_ID_VACCINE = "vaccine"

    fun ensureVaccineChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID_VACCINE,
            "疫苗接种提醒",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "宝宝到龄疫苗接种提醒"
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    fun showVaccine(context: Context, requestCode: Int, babyName: String, doseText: String) {
        ensureVaccineChannel(context)
        val text = "$babyName 到龄了，记得带 TA 接种$doseText"
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_VACCINE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("小脚印 · 疫苗提醒")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(requestCode, notification)
    }

    fun showBirthday(
        context: Context,
        requestCode: Int,
        babyName: String,
        kind: String,
        age: Int,
        leadDays: Int,
        dateText: String
    ) {
        ensureBirthdayChannel(context)
        val kindText = if (kind == BirthdayScheduler.KIND_SOLAR) "阳历生日" else "农历生日"
        val greeting = when (leadDays) {
            3 -> "3 天后（$dateText）是 $babyName 的$kindText"
            1 -> "明天是 $babyName 的$kindText"
            else -> buildString {
                append("今天是")
                append(babyName)
                if (age > 0) append(" $age 岁")
                append(kindText)
                append("，祝")
                append(babyName)
                append("生日快乐！")
            }
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BIRTHDAY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("小脚印 · 生日提醒")
            .setContentText(greeting)
            .setStyle(NotificationCompat.BigTextStyle().bigText(greeting))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(requestCode, notification)
    }
}
