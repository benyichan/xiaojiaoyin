package com.xiaojiaoyin.baby.data

import android.content.Context
import com.xiaojiaoyin.baby.data.db.AppDatabase
import com.xiaojiaoyin.baby.data.repository.BabyRepository
import com.xiaojiaoyin.baby.data.repository.RecordRepository
import com.xiaojiaoyin.baby.data.repository.TodoRepository
import com.xiaojiaoyin.baby.data.settings.SettingsRepository

object AppGraph {
    lateinit var database: AppDatabase
        private set
    lateinit var babyRepository: BabyRepository
        private set
    lateinit var recordRepository: RecordRepository
        private set
    lateinit var todoRepository: TodoRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    fun init(context: Context) {
        if (::database.isInitialized) return
        database = AppDatabase.get(context)
        babyRepository = BabyRepository(database.babyDao())
        recordRepository = RecordRepository(database.recordDao())
        todoRepository = TodoRepository(database.todoDao())
        settingsRepository = SettingsRepository(context.applicationContext)
    }
}
