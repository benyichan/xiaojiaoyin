package com.xiaojiaoyin.baby.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity

class ReminderScheduler(private val context: Context) {

    fun schedule(todo: TodoEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(todo.id, todo.title)
        val canExact = Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()
        try {
            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    todo.timeAt,
                    pendingIntent
                )
            } else {
                // Android 12+ 的 setAlarmClock 同样受精确闹钟权限限制，先试闹钟语义，失败再降级非精确
                runCatching {
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(todo.timeAt, pendingIntent),
                        pendingIntent
                    )
                }.onFailure {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, todo.timeAt, pendingIntent)
                }
            }
        } catch (t: Throwable) {
            android.util.Log.e("Reminder", "schedule failed, fallback to inexact", t)
            runCatching {
                alarmManager.set(AlarmManager.RTC_WAKEUP, todo.timeAt, pendingIntent)
            }
        }
    }

    fun cancel(todoId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.toInt(),
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

    private fun buildPendingIntent(todoId: Long, title: String): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            todoId.toInt(),
            Intent(context, ReminderReceiver::class.java)
                .putExtra(ReminderReceiver.EXTRA_TODO_ID, todoId)
                .putExtra(ReminderReceiver.EXTRA_TITLE, title),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}
