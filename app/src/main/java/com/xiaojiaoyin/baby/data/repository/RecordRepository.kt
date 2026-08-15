package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.db.dao.RecordDao
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import kotlinx.coroutines.flow.Flow

class RecordRepository(private val dao: RecordDao) {
    fun observeRecent(babyId: Long, limit: Int = 20): Flow<List<RecordEntity>> =
        dao.observeRecent(babyId, limit)

    fun observeAll(babyId: Long): Flow<List<RecordEntity>> = dao.observeAll(babyId)

    suspend fun add(
        babyId: Long,
        type: RecordType,
        occurredAt: Long,
        detailJson: String,
        note: String = ""
    ): Long = dao.insert(
        RecordEntity(
            babyId = babyId,
            type = type,
            occurredAt = occurredAt,
            detailJson = detailJson,
            note = note,
            createdAt = System.currentTimeMillis()
        )
    )

    suspend fun countSince(babyId: Long, type: RecordType, since: Long): Int =
        dao.countByTypeSince(babyId, type, since).size

    suspend fun lastOfType(babyId: Long, type: RecordType): RecordEntity? =
        dao.lastOfType(babyId, type)
}
