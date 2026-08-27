package com.xiaojiaoyin.baby.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.domain.LunarCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TODO_ID = "todo_id"
        const val EXTRA_TITLE = "title"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val birthdayKind = intent.getStringExtra(BirthdayScheduler.EXTRA_BIRTHDAY_KIND)
        if (birthdayKind != null) {
            onBirthdayReminder(context, intent, birthdayKind)
            return
        }
        val todoId = intent.getLongExtra(EXTRA_TODO_ID, 0L)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "待办提醒"
        android.util.Log.d("Reminder", "onReceive todoId=$todoId title=$title")
        ReminderNotification.show(context, todoId, title)
        // 标记已提醒（本机状态）：启动重排时据此跳过已提醒的过期待办，避免重复弹通知
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppGraph.database.todoDao().getById(todoId)?.let {
                    AppGraph.todoRepository.markReminded(it)
                }
            } catch (t: Throwable) {
                android.util.Log.e("Reminder", "markReminded failed", t)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun onBirthdayReminder(context: Context, intent: Intent, kind: String) {
        val babyId = intent.getLongExtra(BirthdayScheduler.EXTRA_BABY_ID, 0L)
        val babyName = intent.getStringExtra(BirthdayScheduler.EXTRA_BABY_NAME) ?: "宝宝"
        val leadDays = intent.getIntExtra(BirthdayScheduler.EXTRA_LEAD_DAYS, 0)
        val dateText = intent.getStringExtra(BirthdayScheduler.EXTRA_BIRTHDAY_TEXT) ?: ""
        val requestCode = intent.getIntExtra(BirthdayScheduler.EXTRA_REQUEST_CODE, 0)
        android.util.Log.d("Reminder", "birthday babyId=$babyId kind=$kind")
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val baby = AppGraph.babyRepository.getById(babyId)
                if (baby != null) {
                    val age = LunarCalculator.ageInYears(baby.birthDateTime, System.currentTimeMillis())
                    ReminderNotification.showBirthday(
                        context, requestCode, babyName, kind, age, leadDays, dateText
                    )
                    // 重建该宝宝全部 6 条提醒（已过的档自动排到明年）
                    BirthdayScheduler(context.applicationContext).scheduleForBaby(baby)
                }
            } catch (t: Throwable) {
                android.util.Log.e("Reminder", "birthday notify failed", t)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
