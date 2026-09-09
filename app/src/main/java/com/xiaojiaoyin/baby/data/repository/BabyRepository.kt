package com.xiaojiaoyin.baby.data.repository

import androidx.room.withTransaction
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.dao.BabyDao
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.data.db.entity.SyncTombstoneEntity
import com.xiaojiaoyin.baby.widget.WidgetSync
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class BabyRepository(private val dao: BabyDao) {
    fun observeAll(): Flow<List<BabyEntity>> = dao.observeAll()

    suspend fun getAll(): List<BabyEntity> = dao.getAll()

    suspend fun getById(id: Long): BabyEntity? = dao.getById(id)

    suspend fun add(baby: BabyEntity): Long {
        val id = dao.insert(baby)
        WidgetSync.refresh(AppGraph.appContext)
        return id
    }

    /** 更新时刷新 updatedAt，供同步合并使用 */
    suspend fun update(baby: BabyEntity) {
        dao.update(baby.copy(updatedAt = System.currentTimeMillis()))
        WidgetSync.refresh(AppGraph.appContext)
    }

    suspend fun delete(baby: BabyEntity) {
        AppGraph.database.syncTombstoneDao()
            .upsert(SyncTombstoneEntity("baby", baby.uuid, System.currentTimeMillis()))
        dao.delete(baby)
    }

    /**
     * 级联删除：事务内删除该宝宝的全部记录/待办/学籍/好物/自定义字段后删 baby 行，
     * 并为全部被删行写同步墓碑（防止对端复活）。
     * 返回该宝宝全部照片记录的文件相对路径，供调用方删除磁盘文件。
     */
    suspend fun deleteCascade(baby: BabyEntity): List<String> {
        val db = AppGraph.database
        return db.withTransaction {
            val records = db.recordDao().getAllByBaby(baby.id)
            val photoPaths = records
                .filter { it.type == RecordType.PHOTO }
                .mapNotNull { runCatching { JSONObject(it.detailJson).optString("path", "") }.getOrNull() }
                .filter { it.isNotBlank() }
            val now = System.currentTimeMillis()
            val childTombstones = buildList {
                records.forEach { add(SyncTombstoneEntity("record", it.uuid, now)) }
                db.todoDao().getAllAll().filter { it.babyId == baby.id }
                    .forEach { add(SyncTombstoneEntity("todo", it.uuid, now)) }
                db.schoolStageDao().getAllAll().filter { it.babyId == baby.id }
                    .forEach { add(SyncTombstoneEntity("school", it.uuid, now)) }
                db.goodItemDao().getAllAll().filter { it.babyId == baby.id }
                    .forEach { add(SyncTombstoneEntity("good", it.uuid, now)) }
                db.babyCustomFieldDao().getAllAll().filter { it.babyId == baby.id }
                    .forEach { add(SyncTombstoneEntity("custom_field", it.uuid, now)) }
                db.anniversaryDao().getAllAll().filter { it.babyId == baby.id }
                    .forEach { add(SyncTombstoneEntity("anniversary", it.uuid, now)) }
                db.vaccinationDao().getAllAll().filter { it.babyId == baby.id }
                    .forEach { add(SyncTombstoneEntity("vaccine", it.uuid, now)) }
            }
            db.syncTombstoneDao().upsertAll(childTombstones)
            db.syncTombstoneDao().upsert(SyncTombstoneEntity("baby", baby.uuid, now))
            db.recordDao().deleteByBabyId(baby.id)
            db.todoDao().deleteByBabyId(baby.id)
            db.schoolStageDao().deleteByBabyId(baby.id)
            db.goodItemDao().deleteByBabyId(baby.id)
            db.babyCustomFieldDao().deleteByBabyId(baby.id)
            db.anniversaryDao().deleteByBabyId(baby.id)
            db.vaccinationDao().deleteByBabyId(baby.id)
            dao.delete(baby)
            WidgetSync.refresh(AppGraph.appContext)
            photoPaths
        }
    }
}