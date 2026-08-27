package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.dao.VaccinationDao
import com.xiaojiaoyin.baby.data.db.entity.SyncTombstoneEntity
import com.xiaojiaoyin.baby.data.db.entity.VaccinationEntity
import kotlinx.coroutines.flow.Flow

class VaccinationRepository(private val dao: VaccinationDao) {
    fun observeByBaby(babyId: Long): Flow<List<VaccinationEntity>> = dao.observeByBaby(babyId)

    suspend fun getByDoseKey(babyId: Long, doseKey: String) = dao.getByDoseKey(babyId, doseKey)

    /** 标记已种（已存在则更新，否则新建）；vaccinatedAt=0 表示只标记不填时间 */
    suspend fun markVaccinated(babyId: Long, doseKey: String, vaccinatedAt: Long, note: String) {
        val existing = dao.getByDoseKey(babyId, doseKey)
        val now = System.currentTimeMillis()
        val row = if (existing != null) {
            existing.copy(vaccinatedAt = vaccinatedAt, note = note, updatedAt = now)
        } else {
            VaccinationEntity(
                babyId = babyId,
                doseKey = doseKey,
                vaccinatedAt = vaccinatedAt,
                note = note,
                createdAt = now,
                updatedAt = now
            )
        }
        dao.upsertAll(listOf(row))
    }

    /** 取消已种标记（写墓碑参与同步） */
    suspend fun unmark(item: VaccinationEntity) {
        AppGraph.database.syncTombstoneDao()
            .upsert(SyncTombstoneEntity("vaccine", item.uuid, System.currentTimeMillis()))
        dao.delete(item)
    }
}
