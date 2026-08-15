package com.xiaojiaoyin.baby.domain

import android.content.Context
import android.provider.Settings

object LicenseManager {
    private const val PREFS = "license"
    private const val KEY_DEVICE_ID = "device_id"

    /** 8 位十六进制设备 ID，用于生成/验证激活码 */
    fun deviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DEVICE_ID, null) ?: run {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "unknown"
            val hash = License.hmac(androidId.toByteArray(Charsets.UTF_8))
            val id = hash.take(4).joinToString("") { "%02X".format(it) }
            prefs.edit().putString(KEY_DEVICE_ID, id).apply()
            id
        }
    }

    fun deviceIdBytes(context: Context): ByteArray = hexToBytes(deviceId(context))

    private fun hexToBytes(hex: String): ByteArray {
        require(hex.length % 2 == 0) { "hex 长度必须为偶数" }
        return ByteArray(hex.length / 2) { i ->
            hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
}
