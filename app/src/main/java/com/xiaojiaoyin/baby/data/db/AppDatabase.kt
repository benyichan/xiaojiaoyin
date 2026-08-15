package com.xiaojiaoyin.baby.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xiaojiaoyin.baby.data.db.dao.BabyDao
import com.xiaojiaoyin.baby.data.db.dao.RecordDao
import com.xiaojiaoyin.baby.data.db.dao.TodoDao
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity

@Database(
    entities = [BabyEntity::class, RecordEntity::class, TodoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun recordDao(): RecordDao
    abstract fun todoDao(): TodoDao

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
                ).build().also { instance = it }
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
