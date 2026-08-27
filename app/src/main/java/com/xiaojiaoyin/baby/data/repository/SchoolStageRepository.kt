package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.dao.SchoolStageDao
import com.xiaojiaoyin.baby.data.db.entity.SchoolStageEntity
import com.xiaojiaoyin.baby.data.db.entity.SyncTombstoneEntity
import kotlinx.coroutines.flow.Flow

class SchoolStageRepository(private val dao: SchoolStageDao) {
    fun observeAll(babyId: Long): Flow<List<SchoolStageEntity>> = dao.observeAll(babyId)

    suspend fun add(
        babyId: Long,
        stageType: String,
        className: String,
        schoolName: String,
        teacher: String,
        startAt: String,
        endAt: String,
        studentNo: String,
        costYuan: Double,
        note: String
    ): Long = dao.insert(
        SchoolStageEntity(
            babyId = babyId,
            stageType = stageType,
            className = className,
            schoolName = schoolName,
            teacher = teacher,
            startAt = startAt,
            endAt = endAt,
            studentNo = studentNo,
            costYuan = costYuan,
            note = note,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    )

    suspend fun update(stage: SchoolStageEntity) =
        dao.update(stage.copy(updatedAt = System.currentTimeMillis()))

    suspend fun delete(stage: SchoolStageEntity) {
        AppGraph.database.syncTombstoneDao()
            .upsert(SyncTombstoneEntity("school", stage.uuid, System.currentTimeMillis()))
        dao.delete(stage)
    }
}
