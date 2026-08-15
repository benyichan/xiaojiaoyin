package com.xiaojiaoyin.baby

import android.app.Application
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import com.xiaojiaoyin.baby.data.AppGraph
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
