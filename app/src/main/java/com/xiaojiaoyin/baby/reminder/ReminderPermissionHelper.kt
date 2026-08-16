package com.xiaojiaoyin.baby.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object ReminderPermissionHelper {

    fun needsNotificationPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED

    fun requestNotificationPermission(activity: ComponentActivity) {
        if (needsNotificationPermission(activity)) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
    }

    fun canScheduleExact(context: Context): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()
    }

    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= 31 && !canScheduleExact(context)) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            )
        }
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun openBatterySettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        )
    }

    /** 国产 ROM 自启动引导文案；返回 null 表示无需提示 */
    fun vendorHint(context: Context): String? {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") ->
                "小米/红米手机：请在「安全中心 → 应用管理 → 权限 → 自启动」中允许小脚印自启动，否则后台提醒可能失效。"
            manufacturer.contains("huawei") || manufacturer.contains("honor") ->
                "华为/荣耀手机：请在「手机管家 → 应用启动管理」中关闭小脚印的自动管理，并允许自启动。"
            manufacturer.contains("oppo") || manufacturer.contains("realme") ||
                manufacturer.contains("oneplus") ->
                "OPPO/一加/realme 手机：请在「设置 → 应用 → 小脚印 → 允许自启动」中开启，并允许后台运行。"
            manufacturer.contains("vivo") || manufacturer.contains("iqoo") ->
                "vivo/iQOO 手机：请在「i管家 → 应用管理 → 权限管理 → 自启动」中允许小脚印自启动。"
            manufacturer.contains("meizu") ->
                "魅族手机：请在「设置 → 应用管理 → 小脚印 → 权限管理」允许通知与自启动，并到「手机管家 → 应用管理 → 小脚印」允许后台运行，否则提醒可能被拦截。"
            else -> null
        }
    }

    fun showGuideDialog(context: Context, onOpenSettings: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("开启提醒权限")
            .setMessage("为了确保到点提醒准时送达，请允许以下设置：通知权限、精确闹钟、电池优化白名单。")
            .setPositiveButton("去设置") { _, _ -> onOpenSettings() }
            .setNegativeButton("稍后", null)
            .show()
    }
}
