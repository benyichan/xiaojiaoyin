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

    /** 压缩为最长边 512px 存 avatars/ 目录，返回相对路径 avatars/xxx.jpg；供宝宝头像使用 */
    fun saveAvatar(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "avatars").apply { mkdirs() }
            val fileName = "avatar-${System.currentTimeMillis()}.jpg"
            val outFile = File(dir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                val bmp = BitmapFactory.decodeStream(input) ?: return null
                val scale = min(1f, 512f / max(bmp.width, bmp.height))
                val w = (bmp.width * scale).toInt().coerceAtLeast(1)
                val h = (bmp.height * scale).toInt().coerceAtLeast(1)
                val scaled = Bitmap.createScaledBitmap(bmp, w, h, true)
                outFile.outputStream().use { out ->
                    scaled.compress(Bitmap.CompressFormat.JPEG, 88, out)
                }
                if (scaled != bmp) bmp.recycle()
                scaled.recycle()
            }
            "avatars/$fileName"
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 按目标边长采样解码缩略图（inSampleSize 为 2 的幂）。
     * 供列表/网格缩略展示使用，避免主线程全尺寸解码 OOM。
     */
    fun decodeThumb(file: File, targetEdge: Int = 400): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (bounds.outWidth / sample > targetEdge || bounds.outHeight / sample > targetEdge) {
            sample *= 2
        }
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        return BitmapFactory.decodeFile(file.absolutePath, opts)
    }

    fun delete(context: Context, path: String) {
        runCatching { File(context.filesDir, path).delete() }
    }
}
