package com.xiaojiaoyin.baby.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import kotlin.math.max
import kotlin.math.min

object PhotoStorage {
    private const val MAX_EDGE = 1600

    /** 从 content Uri 读取、压缩并保存到应用私有目录，返回相对路径 photos/xxx.jpg */
    fun saveImage(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "photos").apply { mkdirs() }
            val fileName = "photo-${System.currentTimeMillis()}.jpg"
            val outFile = File(dir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                val bmp = BitmapFactory.decodeStream(input) ?: return null
                val scale = min(1f, MAX_EDGE.toFloat() / max(bmp.width, bmp.height))
                val w = (bmp.width * scale).toInt().coerceAtLeast(1)
                val h = (bmp.height * scale).toInt().coerceAtLeast(1)
                val scaled = Bitmap.createScaledBitmap(bmp, w, h, true)
                outFile.outputStream().use { out ->
                    scaled.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
                if (scaled != bmp) bmp.recycle()
                scaled.recycle()
            }
            "photos/$fileName"
        } catch (e: Exception) {
            null
        }
    }

    fun loadFile(context: Context, path: String): File = File(context.filesDir, path)

    fun delete(context: Context, path: String) {
        runCatching { File(context.filesDir, path).delete() }
    }
}
