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
        if (canExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                todo.timeAt,
                pendingIntent
            )
        } else {
            // 精确闹钟权限不可用时用 setAlarmClock 兜底：闹钟语义必达，无需 SCHEDULE_EXACT_ALARM
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(todo.timeAt, pendingIntent),
                pendingIntent
            )
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
