package com.xiaojiaoyin.baby

import android.app.Application
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.reminder.BirthdayScheduler
import com.xiaojiaoyin.baby.widget.WidgetSync
import com.xiaojiaoyin.baby.reminder.ReminderScheduler
import com.xiaojiaoyin.baby.reminder.VaccineScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date

class BabyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
        installCrashLogger()
        CoroutineScope(Dispatchers.IO).launch {
            AppGraph.proStatusRepository.ensureTrialStarted()
            // 启动时兜底刷新小组件（覆盖恢复备份重启、开机等场景）
            runCatching { WidgetSync.refresh(this@BabyApp) }
            runCatching {
                BirthdayScheduler(this@BabyApp).scheduleAll()
            }.onFailure {
                android.util.Log.e("BirthdayScheduler", "scheduleAll failed", it)
            }
            // force-stop / 被杀进程会清掉全部闹钟：启动时重排待办提醒。
            // 已过期的会立即触发补发一次（remindedAt 防止之后重复骚扰）
            runCatching {
                val scheduler = ReminderScheduler(this@BabyApp)
                AppGraph.todoRepository.pendingReminders().forEach { scheduler.schedule(it) }
            }.onFailure {
                android.util.Log.e("ReminderScheduler", "reschedule failed", it)
            }
            runCatching {
                VaccineScheduler(this@BabyApp).scheduleAll()
            }.onFailure {
                android.util.Log.e("VaccineScheduler", "reschedule failed", it)
            }
        }
    }

    /** 捕获崩溃并把堆栈写到「下载」文件夹，便于真机排查 */
    private fun installCrashLogger() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val content = "时间：${Date()}\n线程：${thread.name}\n\n$sw\n"
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "xiaojiaoyin-crash-${System.currentTimeMillis()}.txt")
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { os ->
                        os.write(content.toByteArray(Charsets.UTF_8))
                    }
                }
            } catch (_: Exception) {
                // 日志写入失败不影响崩溃流程
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
