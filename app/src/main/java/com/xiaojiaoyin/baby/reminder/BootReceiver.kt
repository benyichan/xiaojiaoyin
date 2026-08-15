package com.xiaojiaoyin.baby.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xiaojiaoyin.baby.data.AppGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pending = AppGraph.todoRepository.pendingReminders(System.currentTimeMillis())
                val scheduler = ReminderScheduler(context)
                pending.forEach { scheduler.schedule(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
