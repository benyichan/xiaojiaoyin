package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.dao.BabyCustomFieldDao
import com.xiaojiaoyin.baby.data.db.entity.BabyCustomFieldEntity
import com.xiaojiaoyin.baby.data.db.entity.SyncTombstoneEntity
import kotlinx.coroutines.flow.Flow

class BabyCustomFieldRepository(private val dao: BabyCustomFieldDao) {
    fun observeAll(babyId: Long): Flow<List<BabyCustomFieldEntity>> = dao.observeAll(babyId)

    suspend fun add(babyId: Long, key: String, value: String): Long =
        dao.insert(
            BabyCustomFieldEntity(
                babyId = babyId,
                fieldKey = key,
                fieldValue = value,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )

    suspend fun update(field: BabyCustomFieldEntity) =
        dao.update(field.copy(updatedAt = System.currentTimeMillis()))

    suspend fun delete(field: BabyCustomFieldEntity) {
        AppGraph.database.syncTombstoneDao()
            .upsert(SyncTombstoneEntity("custom_field", field.uuid, System.currentTimeMillis()))
        dao.delete(field)
    }
}
