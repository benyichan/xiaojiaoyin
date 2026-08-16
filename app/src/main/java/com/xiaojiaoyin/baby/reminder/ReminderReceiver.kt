package com.xiaojiaoyin.baby.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TODO_ID = "todo_id"
        const val EXTRA_TITLE = "title"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getLongExtra(EXTRA_TODO_ID, 0L)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "待办提醒"
        android.util.Log.d("Reminder", "onReceive todoId=$todoId title=$title")
        ReminderNotification.show(context, todoId, title)
    }
}
