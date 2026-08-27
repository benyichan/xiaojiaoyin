package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.dao.AnniversaryDao
import com.xiaojiaoyin.baby.data.db.entity.AnniversaryEntity
import com.xiaojiaoyin.baby.data.db.entity.SyncTombstoneEntity
import kotlinx.coroutines.flow.Flow

class AnniversaryRepository(private val dao: AnniversaryDao) {
    fun observeByBaby(babyId: Long): Flow<List<AnniversaryEntity>> = dao.observeByBaby(babyId)

    suspend fun getById(id: Long): AnniversaryEntity? = dao.getById(id)

    suspend fun add(
        babyId: Long,
        name: String,
        dateAt: Long,
        repeatYearly: Boolean,
        note: String
    ): Long = dao.insert(
        AnniversaryEntity(
            babyId = babyId,
            name = name,
            dateAt = dateAt,
            repeatYearly = repeatYearly,
            note = note,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    )

    suspend fun update(item: AnniversaryEntity) =
        dao.update(item.copy(updatedAt = System.currentTimeMillis()))

    suspend fun delete(item: AnniversaryEntity) {
        AppGraph.database.syncTombstoneDao()
            .upsert(SyncTombstoneEntity("anniversary", item.uuid, System.currentTimeMillis()))
        dao.delete(item)
    }
}
