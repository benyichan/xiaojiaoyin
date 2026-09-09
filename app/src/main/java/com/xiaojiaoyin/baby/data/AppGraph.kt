package com.xiaojiaoyin.baby.data

import android.content.Context
import com.xiaojiaoyin.baby.data.db.AppDatabase
import com.xiaojiaoyin.baby.data.repository.AnniversaryRepository
import com.xiaojiaoyin.baby.data.repository.BabyRepository
import com.xiaojiaoyin.baby.data.repository.BabyCustomFieldRepository
import com.xiaojiaoyin.baby.data.repository.GoodItemRepository
import com.xiaojiaoyin.baby.data.repository.RecordRepository
import com.xiaojiaoyin.baby.data.repository.SchoolStageRepository
import com.xiaojiaoyin.baby.data.repository.TodoRepository
import com.xiaojiaoyin.baby.data.repository.VaccinationRepository
import com.xiaojiaoyin.baby.data.settings.SettingsRepository
import com.xiaojiaoyin.baby.data.settings.ProStatusRepository

object AppGraph {
    /** 小组件等非 Activity 入口的冷启动判断 */
    val isInitialized: Boolean get() = ::database.isInitialized
    lateinit var appContext: Context
        private set
    lateinit var database: AppDatabase
        private set
    lateinit var babyRepository: BabyRepository
        private set
    lateinit var recordRepository: RecordRepository
        private set
    lateinit var todoRepository: TodoRepository
        private set
    lateinit var schoolStageRepository: SchoolStageRepository
        private set
    lateinit var goodItemRepository: GoodItemRepository
        private set
    lateinit var babyCustomFieldRepository: BabyCustomFieldRepository
        private set
    lateinit var anniversaryRepository: AnniversaryRepository
        private set
    lateinit var vaccinationRepository: VaccinationRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set
    lateinit var proStatusRepository: ProStatusRepository
        private set

    fun init(context: Context) {
        if (::database.isInitialized) return
        appContext = context.applicationContext
        database = AppDatabase.get(context)
        babyRepository = BabyRepository(database.babyDao())
        recordRepository = RecordRepository(database.recordDao())
        todoRepository = TodoRepository(database.todoDao())
        schoolStageRepository = SchoolStageRepository(database.schoolStageDao())
        goodItemRepository = GoodItemRepository(database.goodItemDao())
        babyCustomFieldRepository = BabyCustomFieldRepository(database.babyCustomFieldDao())
        anniversaryRepository = AnniversaryRepository(database.anniversaryDao())
        vaccinationRepository = VaccinationRepository(database.vaccinationDao())
        settingsRepository = SettingsRepository(context.applicationContext)
        proStatusRepository = ProStatusRepository(context.applicationContext)
    }
}
