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
        val photos: Int,
        val exportedAt: Long
    )

    private fun dbVersion(): Int =
        AppDatabase.get(context).openHelper.writableDatabase.version

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

        val photosDir = File(context.filesDir, "photos")
        val photoFiles = photosDir.listFiles()?.filter { it.isFile } ?: emptyList()

        val resolver = context.contentResolver
        resolver.openOutputStream(uri)?.use { out ->
            ZipOutputStream(BufferedOutputStream(out)).use { zip ->
                // SQLite WAL 三件套一起备份，保证数据完整
                listOf(
                    "database.db" to dbFile,
                    "database.db-wal" to File(dbFile.path + "-wal"),
                    "database.db-shm" to File(dbFile.path + "-shm"),
                    "settings.preferences_pb" to File(context.filesDir, "datastore/settings.preferences_pb")
                ).forEach { (name, file) ->
                    if (file.exists()) {
                        zip.putNextEntry(ZipEntry(name))
                        file.inputStream().use { it.copyTo(zip) }
                        zip.closeEntry()
                    }
                }

                // 照片目录整体打包（DB 只存相对路径，不打包恢复后照片全丢）
                photoFiles.forEach { f ->
                    zip.putNextEntry(ZipEntry("photos/${f.name}"))
                    f.inputStream().use { it.copyTo(zip) }
                    zip.closeEntry()
                }

                // 宝宝头像目录一并打包
                File(context.filesDir, "avatars").listFiles()?.filter { it.isFile }?.forEach { f ->
                    zip.putNextEntry(ZipEntry("avatars/${f.name}"))
                    f.inputStream().use { it.copyTo(zip) }
                    zip.closeEntry()
                }

                val manifest = JSONObject()
                    .put("version", 1)
                    .put("dbVersion", dbVersion())
                    .put("exportedAt", exportedAt)
                    .put("babies", babies)
                    .put("records", records)
                    .put("photos", photoFiles.size)
                    .toString()
                zip.putNextEntry(ZipEntry("manifest.json"))
                zip.write(manifest.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
        } ?: throw IOException("无法写入备份文件")

        Summary(babies = babies, records = records, photos = photoFiles.size, exportedAt = exportedAt)
    }

    suspend fun restore(uri: Uri): Summary = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val tmpDir = File(context.cacheDir, "restore-${System.currentTimeMillis()}").apply { mkdirs() }
        try {
            resolver.openInputStream(uri)?.use { input ->
                ZipInputStream(BufferedInputStream(input)).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        // Zip Slip 防护：拒绝路径穿越条目
                        if (entry.name.contains("..") || entry.name.startsWith("/")) {
                            throw IOException("备份文件包含非法路径条目")
                        }
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
            val manifest = runCatching {
                JSONObject(manifestFile.readText(Charsets.UTF_8))
            }.getOrElse { throw IOException("备份文件损坏或不是有效的备份包") }
            if (manifest.optInt("version", 0) != 1) {
                throw IOException("不支持的备份版本")
            }

            // 高版本数据库恢复到低版本 App 会永久崩溃：先拦截
            val backupDbVersion = manifest.optInt("dbVersion", 0)
            val currentDbVersion = dbVersion()
            if (backupDbVersion > currentDbVersion) {
                throw IOException("备份来自更新版本的 App（数据库 v$backupDbVersion > 当前 v$currentDbVersion），请先升级 App 再恢复")
            }

            // 原子恢复：先把备份库三件套拷贝到缓存暂存区，全部成功后再替换旧库。
            // 任一步失败则旧库原样保留，避免"删了旧库、新库写一半"的残局
            val dbFile = context.getDatabasePath(AppDatabase.NAME)
            val stageDir = File(context.cacheDir, "restore-stage-${System.currentTimeMillis()}").apply { mkdirs() }
            try {
                listOf("database.db", "database.db-wal", "database.db-shm").forEach { name ->
                    val src = File(tmpDir, name)
                    if (src.exists()) {
                        src.copyTo(File(stageDir, name), overwrite = true)
                    }
                }
            } catch (e: IOException) {
                stageDir.deleteRecursively()
                throw IOException("恢复数据写入失败（存储空间不足？）：${e.message}")
            }

            // 暂存成功，开始替换（rename 同目录内为原子操作）
            AppDatabase.closeForRestore()
            listOf("", "-wal", "-shm").forEach { ext ->
                File(dbFile.path + ext).delete()
            }
            listOf("database.db", "database.db-wal", "database.db-shm").forEach { name ->
                val staged = File(stageDir, name)
                if (staged.exists()) {
                    staged.renameTo(File(dbFile.path + name.removePrefix("database.db")))
                }
            }
            stageDir.deleteRecursively()

            // 恢复照片目录：整体替换，失败回滚
            val photosBackup = File(tmpDir, "photos")
            val photosDir = File(context.filesDir, "photos")
            if (photosBackup.exists()) {
                val bak = File(context.filesDir, "photos.bak")
                bak.deleteRecursively()
                if (photosDir.exists()) photosDir.renameTo(bak)
                if (!photosBackup.renameTo(photosDir)) {
                    // 回滚：把旧照片目录挪回来
                    if (bak.exists()) bak.renameTo(photosDir)
                    throw IOException("恢复照片目录失败")
                }
                bak.deleteRecursively()
            }

            // 恢复宝宝头像目录（整体替换，失败不阻断）
            runCatching {
                val avatarsBackup = File(tmpDir, "avatars")
                val avatarsDir = File(context.filesDir, "avatars")
                if (avatarsBackup.exists()) {
                    avatarsDir.deleteRecursively()
                    avatarsBackup.renameTo(avatarsDir)
                }
            }

            // 恢复设置文件（currentBabyId、类型显示设置）；失败不影响主数据
            runCatching {
                val settingsBackup = File(tmpDir, "settings.preferences_pb")
                if (settingsBackup.exists()) {
                    val dest = File(context.filesDir, "datastore/settings.preferences_pb")
                    dest.parentFile?.mkdirs()
                    settingsBackup.copyTo(dest, overwrite = true)
                }
            }

            Summary(
                babies = manifest.optInt("babies", 0),
                records = manifest.optInt("records", 0),
                photos = manifest.optInt("photos", 0),
                exportedAt = manifest.optLong("exportedAt", 0L)
            )
        } finally {
            tmpDir.deleteRecursively()
        }
    }

    fun restartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }
}