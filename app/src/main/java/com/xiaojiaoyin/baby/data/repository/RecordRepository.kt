package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.dao.RecordDao
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.data.db.entity.SyncTombstoneEntity
import kotlinx.coroutines.flow.Flow

class RecordRepository(private val dao: RecordDao) {
    fun observeRecent(babyId: Long, limit: Int = 20): Flow<List<RecordEntity>> =
        dao.observeRecent(babyId, limit)

    fun observeAll(babyId: Long): Flow<List<RecordEntity>> = dao.observeAll(babyId)

    fun observeByType(babyId: Long, type: RecordType): Flow<List<RecordEntity>> =
        dao.observeByType(babyId, type)

    fun observeByParent(parentId: Long): Flow<List<RecordEntity>> =
        dao.observeByParent(parentId)

    suspend fun getByParent(parentId: Long): List<RecordEntity> =
        dao.getByParent(parentId)

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
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    )

    suspend fun countSince(babyId: Long, type: RecordType, since: Long): Int =
        dao.countByTypeSince(babyId, type, since).size

    suspend fun lastOfType(babyId: Long, type: RecordType): RecordEntity? =
        dao.lastOfType(babyId, type)

    suspend fun delete(record: RecordEntity) {
        AppGraph.database.syncTombstoneDao()
            .upsert(SyncTombstoneEntity("record", record.uuid, System.currentTimeMillis()))
        dao.delete(record)
    }

    suspend fun update(record: RecordEntity) = updateWithTimestamp(record)

    /** 更新记录时刷新 updatedAt，供同步合并使用 */
    suspend fun updateWithTimestamp(record: RecordEntity): Unit =
        dao.update(record.copy(updatedAt = System.currentTimeMillis()))

    /** 节点配图：保存为相册照片并自动打「关键时刻」标签 */
    suspend fun addNodePhoto(
        babyId: Long,
        nodeId: Long,
        path: String,
        occurredAt: Long,
        note: String = ""
    ): Long = dao.insert(
        RecordEntity(
            babyId = babyId,
            type = RecordType.PHOTO,
            occurredAt = occurredAt,
            detailJson = org.json.JSONObject().put("path", path).toString(),
            note = note,
            tags = com.xiaojiaoyin.baby.data.db.entity.TAG_KEY_MOMENT,
            parentId = nodeId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    )

    suspend fun addMedical(
        babyId: Long,
        category: String,
        title: String,
        detail: String,
        costYuan: Double,
        occurredAt: Long
    ): Long = add(
        babyId = babyId,
        type = RecordType.MEDICAL,
        occurredAt = occurredAt,
        detailJson = org.json.JSONObject()
            .put("category", category)
            .put("title", title)
            .put("detail", detail)
            .put("costYuan", costYuan)
            .toString()
    )
}
