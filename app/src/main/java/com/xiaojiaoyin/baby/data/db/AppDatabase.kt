package com.xiaojiaoyin.baby.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xiaojiaoyin.baby.data.db.dao.BabyDao
import com.xiaojiaoyin.baby.data.db.dao.GoodItemDao
import com.xiaojiaoyin.baby.data.db.dao.RecordDao
import com.xiaojiaoyin.baby.data.db.dao.SchoolStageDao
import com.xiaojiaoyin.baby.data.db.dao.TodoDao
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.data.db.entity.GoodItemEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.SchoolStageEntity
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity

@Database(
    entities = [
        BabyEntity::class,
        RecordEntity::class,
        TodoEntity::class,
        SchoolStageEntity::class,
        GoodItemEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun recordDao(): RecordDao
    abstract fun todoDao(): TodoDao
    abstract fun schoolStageDao(): SchoolStageDao
    abstract fun goodItemDao(): GoodItemDao

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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { instance = it }
            }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE record ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
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

        /** 备份恢复专用：关闭并清空单例，供恢复后重启进程使用 */
        fun closeForRestore() {
            synchronized(this) {
                instance?.close()
                instance = null
            }
        }
    }
}
