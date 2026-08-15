package com.xiaojiaoyin.baby.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManager(private val context: Context) {

    data class Summary(
        val babies: Int,
        val records: Int,
        val exportedAt: Long
    )

    suspend fun export(uri: Uri): Summary = withContext(Dispatchers.IO) {
        // 先合并 WAL，减少备份体积
        runCatching {
            val db = AppDatabase.get(context).openHelper.writableDatabase
            db.query("PRAGMA wal_checkpoint(FULL)").close()
        }

        val dbFile = context.getDatabasePath(AppDatabase.NAME)
        val babies = AppGraph.babyRepository.getAll().size
        val records = AppGraph.database.recordDao().countAll()
        val exportedAt = System.currentTimeMillis()

        val resolver = context.contentResolver
        resolver.openOutputStream(uri)?.use { out ->
            ZipOutputStream(BufferedOutputStream(out)).use { zip ->
                // SQLite WAL 三件套一起备份，保证数据完整
                listOf(
                    "database.db" to dbFile,
                    "database.db-wal" to File(dbFile.path + "-wal"),
                    "database.db-shm" to File(dbFile.path + "-shm")
                ).forEach { (name, file) ->
                    if (file.exists()) {
                        zip.putNextEntry(ZipEntry(name))
                        file.inputStream().use { it.copyTo(zip) }
                        zip.closeEntry()
                    }
                }

                val manifest = JSONObject()
                    .put("version", 1)
                    .put("exportedAt", exportedAt)
                    .put("babies", babies)
                    .put("records", records)
                    .toString()
                zip.putNextEntry(ZipEntry("manifest.json"))
                zip.write(manifest.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
        } ?: throw IOException("无法写入备份文件")

        Summary(babies = babies, records = records, exportedAt = exportedAt)
    }

    suspend fun restore(uri: Uri): Summary = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val tmpDir = File(context.cacheDir, "restore-${System.currentTimeMillis()}").apply { mkdirs() }

        resolver.openInputStream(uri)?.use { input ->
            ZipInputStream(BufferedInputStream(input)).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val outFile = File(tmpDir, entry.name)
                    outFile.outputStream().use { zip.copyTo(it) }
                    entry = zip.nextEntry
                }
            }
        } ?: throw IOException("无法读取备份文件")

        val manifestFile = File(tmpDir, "manifest.json")
        val dbBackup = File(tmpDir, "database.db")
        if (!manifestFile.exists() || !dbBackup.exists()) {
            throw IOException("备份文件不完整")
        }
        val manifest = JSONObject(manifestFile.readText(Charsets.UTF_8))
        if (manifest.optInt("version", 0) != 1) {
            throw IOException("不支持的备份版本")
        }

        // 关闭数据库并替换三件套
        AppDatabase.closeForRestore()
        val dbFile = context.getDatabasePath(AppDatabase.NAME)
        listOf("", "-wal", "-shm").forEach { ext ->
            File(dbFile.path + ext).delete()
        }
        listOf("database.db", "database.db-wal", "database.db-shm").forEach { name ->
            val src = File(tmpDir, name)
            if (src.exists()) {
                src.copyTo(File(dbFile.path + name.removePrefix("database.db")), overwrite = true)
            }
        }
        tmpDir.deleteRecursively()

        Summary(
            babies = manifest.optInt("babies", 0),
            records = manifest.optInt("records", 0),
            exportedAt = manifest.optLong("exportedAt", 0L)
        )
    }

    fun restartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }
}
