package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.db.dao.SchoolStageDao
import com.xiaojiaoyin.baby.data.db.entity.SchoolStageEntity
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
            note = note,
            createdAt = System.currentTimeMillis()
        )
    )

    suspend fun update(stage: SchoolStageEntity) = dao.update(stage)

    suspend fun delete(stage: SchoolStageEntity) = dao.delete(stage)
}
