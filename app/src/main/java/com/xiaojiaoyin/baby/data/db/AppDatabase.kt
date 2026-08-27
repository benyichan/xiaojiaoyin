package com.xiaojiaoyin.baby.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xiaojiaoyin.baby.data.db.dao.AnniversaryDao
import com.xiaojiaoyin.baby.data.db.dao.BabyDao
import com.xiaojiaoyin.baby.data.db.dao.BabyCustomFieldDao
import com.xiaojiaoyin.baby.data.db.dao.GoodItemDao
import com.xiaojiaoyin.baby.data.db.dao.RecordDao
import com.xiaojiaoyin.baby.data.db.dao.SchoolStageDao
import com.xiaojiaoyin.baby.data.db.dao.SyncTombstoneDao
import com.xiaojiaoyin.baby.data.db.dao.TodoDao
import com.xiaojiaoyin.baby.data.db.dao.VaccinationDao
import com.xiaojiaoyin.baby.data.db.entity.AnniversaryEntity
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.data.db.entity.BabyCustomFieldEntity
import com.xiaojiaoyin.baby.data.db.entity.GoodItemEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.SchoolStageEntity
import com.xiaojiaoyin.baby.data.db.entity.SyncTombstoneEntity
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import com.xiaojiaoyin.baby.data.db.entity.VaccinationEntity
import java.util.UUID

@Database(
    entities = [
        BabyEntity::class,
        RecordEntity::class,
        TodoEntity::class,
        SchoolStageEntity::class,
        GoodItemEntity::class,
        BabyCustomFieldEntity::class,
        SyncTombstoneEntity::class,
        AnniversaryEntity::class,
        VaccinationEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun recordDao(): RecordDao
    abstract fun todoDao(): TodoDao
    abstract fun schoolStageDao(): SchoolStageDao
    abstract fun goodItemDao(): GoodItemDao
    abstract fun babyCustomFieldDao(): BabyCustomFieldDao
    abstract fun syncTombstoneDao(): SyncTombstoneDao
    abstract fun anniversaryDao(): AnniversaryDao
    abstract fun vaccinationDao(): VaccinationDao

    companion object {
        const val NAME = "baby-app.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NAME
                )
                    .addMigrations(
                        MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5,
                        MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9
                    )
                    .build()
                    .also { instance = it }
            }

        /**
         * v6 → v7：同步协议升级
         * - 6 张业务表增加 uuid（同步唯一键，跨设备稳定）
         * - 5 张表增加 updatedAt（合并用；存量行回填 = createdAt）
         * - 新建 sync_tombstone 墓碑表
         * - uuid 索引（merge 时按 uuid 查询）
         */
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                listOf("baby", "record", "todo", "school_stage", "good_item", "baby_custom_field").forEach { t ->
                    db.execSQL("ALTER TABLE `$t` ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")
                    db.execSQL("CREATE INDEX IF NOT EXISTS idx_${t}_uuid ON `$t`(uuid)")
                }
                listOf("baby", "todo", "school_stage", "good_item", "baby_custom_field").forEach { t ->
                    db.execSQL("ALTER TABLE `$t` ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                }
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `sync_tombstone` (" +
                        "`tableName` TEXT NOT NULL, " +
                        "`uuid` TEXT NOT NULL, " +
                        "`deletedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`tableName`, `uuid`))"
                )
                // 存量行回填 uuid（SQLite 无 UUID 函数，Kotlin 侧逐行生成）
                listOf("baby", "record", "todo", "school_stage", "good_item", "baby_custom_field").forEach { t ->
                    db.query("SELECT id FROM `$t` WHERE uuid = ''").use { cursor ->
                        while (cursor.moveToNext()) {
                            val id = cursor.getLong(0)
                            db.execSQL(
                                "UPDATE `$t` SET uuid = ? WHERE id = ?",
                                arrayOf(UUID.randomUUID().toString(), id)
                            )
                        }
                    }
                    db.execSQL("UPDATE `$t` SET updatedAt = createdAt WHERE updatedAt = 0 AND createdAt > 0")
                }
            }
        }

        /**
         * v7 → v8：待办提醒补发
         * - todo 增加 remindedAt（本机已提醒时间戳，不参与同步）
         * - 用于启动重排时识别"已提醒过"的过期待办，避免每次打开 App 重复弹通知
         */
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE todo ADD COLUMN remindedAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * v8 → v9：纪念日 + 疫苗接种 + 宝宝头像
         * - 新表 anniversary（纪念日倒计时）
         * - 新表 vaccination_status（疫苗接种状态，日程内置 assets）
         * - baby 增加 avatarPath（本机头像相对路径，不参与同步）
         */
        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `anniversary` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`uuid` TEXT NOT NULL, " +
                        "`babyId` INTEGER NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`dateAt` INTEGER NOT NULL, " +
                        "`repeatYearly` INTEGER NOT NULL, " +
                        "`note` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, " +
                        "`updatedAt` INTEGER NOT NULL)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_anniversary_uuid ON `anniversary`(uuid)")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `vaccination_status` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`uuid` TEXT NOT NULL, " +
                        "`babyId` INTEGER NOT NULL, " +
                        "`doseKey` TEXT NOT NULL, " +
                        "`vaccinatedAt` INTEGER NOT NULL, " +
                        "`note` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, " +
                        "`updatedAt` INTEGER NOT NULL)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_vaccination_status_uuid ON `vaccination_status`(uuid)")
                db.execSQL("ALTER TABLE baby ADD COLUMN avatarPath TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE record ADD COLUMN tags TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE record ADD COLUMN parentId INTEGER")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE record ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `school_stage` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`babyId` INTEGER NOT NULL, " +
                        "`stageType` TEXT NOT NULL, " +
                        "`className` TEXT NOT NULL, " +
                        "`schoolName` TEXT NOT NULL, " +
                        "`teacher` TEXT NOT NULL, " +
                        "`startAt` TEXT NOT NULL, " +
                        "`endAt` TEXT NOT NULL, " +
                        "`studentNo` TEXT NOT NULL, " +
                        "`note` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `good_item` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`babyId` INTEGER NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`rating` INTEGER NOT NULL, " +
                        "`note` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE good_item ADD COLUMN category TEXT NOT NULL DEFAULT '其他'")
                db.execSQL("ALTER TABLE good_item ADD COLUMN priceYuan REAL NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE good_item ADD COLUMN buyDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE school_stage ADD COLUMN costYuan REAL NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `baby_custom_field` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`babyId` INTEGER NOT NULL, " +
                        "`fieldKey` TEXT NOT NULL, " +
                        "`fieldValue` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL)"
                )
            }
        }

        /** 备份恢复专用：关闭并清空单例，供恢复后重启进程使用 */
        fun closeForRestore() {
            synchronized(this) {
                instance?.close()
                instance = null
            }
        }
    }
}
