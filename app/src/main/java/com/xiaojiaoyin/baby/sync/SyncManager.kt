package com.xiaojiaoyin.baby.sync

import androidx.room.withTransaction
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import org.json.JSONObject

class SyncManager {

    /** 提取本机全部数据为变更集（含墓碑），外键以 uuid 引用传输 */
    suspend fun extractChanges(): List<SyncChange> {
        val db = AppGraph.database
        val babyUuidById = db.babyDao().getAll().associate { it.id to it.uuid }
        val recordUuidById = db.recordDao().getAllAll().associate { it.id to it.uuid }

        return buildList {
            db.babyDao().getAll().forEach {
                add(SyncChange("baby", it.uuid, it.updatedAt, json = SyncJson.babyToJson(it)))
            }
            db.recordDao().getAllAll().forEach {
                add(
                    SyncChange(
                        "record", it.uuid, it.updatedAt,
                        json = SyncJson.recordToJson(
                            it,
                            babyUuid = babyUuidById[it.babyId] ?: "",
                            parentUuid = it.parentId?.let { pid -> recordUuidById[pid] }
                        )
                    )
                )
            }
            db.todoDao().getAllAll().forEach {
                add(SyncChange("todo", it.uuid, it.updatedAt, json = SyncJson.todoToJson(it, babyUuidById[it.babyId] ?: "")))
            }
            db.schoolStageDao().getAllAll().forEach {
                add(SyncChange("school", it.uuid, it.updatedAt, json = SyncJson.schoolToJson(it, babyUuidById[it.babyId] ?: "")))
            }
            db.goodItemDao().getAllAll().forEach {
                add(SyncChange("good", it.uuid, it.updatedAt, json = SyncJson.goodToJson(it, babyUuidById[it.babyId] ?: "")))
            }
            db.babyCustomFieldDao().getAllAll().forEach {
                add(SyncChange("custom_field", it.uuid, it.updatedAt, json = SyncJson.customFieldToJson(it, babyUuidById[it.babyId] ?: "")))
            }
            db.anniversaryDao().getAllAll().forEach {
                add(SyncChange("anniversary", it.uuid, it.updatedAt, json = SyncJson.anniversaryToJson(it, babyUuidById[it.babyId] ?: "")))
            }
            db.vaccinationDao().getAllAll().forEach {
                add(SyncChange("vaccine", it.uuid, it.updatedAt, json = SyncJson.vaccineToJson(it, babyUuidById[it.babyId] ?: "")))
            }
            db.syncTombstoneDao().getAllAll().forEach {
                add(SyncChange(it.tableName, it.uuid, it.deletedAt, deleted = true, json = "{}"))
            }
        }
    }

    /**
     * 应用合并后的变更集：单事务内完成，任一步失败整体回滚，避免半同步状态。
     * 流程：墓碑删除（baby 墓碑级联子表）→ baby upsert 建映射 → record 两遍
     * （先插后回填 parentId）→ 其余子表按 babyUuid 映射落库。
     */
    suspend fun applyChanges(changes: List<SyncChange>) {
        val db = AppGraph.database
        db.withTransaction {
            // 1) 墓碑
            changes.filter { it.deleted }.forEach { t ->
                when (t.table) {
                    "baby" -> db.babyDao().getByUuid(t.uuid)?.let { baby ->
                        db.recordDao().deleteByBabyId(baby.id)
                        db.todoDao().deleteByBabyId(baby.id)
                        db.schoolStageDao().deleteByBabyId(baby.id)
                        db.goodItemDao().deleteByBabyId(baby.id)
                        db.babyCustomFieldDao().deleteByBabyId(baby.id)
                        db.babyDao().delete(baby)
                    }
                    "record" -> db.recordDao().getByUuid(t.uuid)?.let { db.recordDao().delete(it) }
                    "todo" -> db.todoDao().getByUuid(t.uuid)?.let { db.todoDao().delete(it) }
                    "school" -> db.schoolStageDao().getByUuid(t.uuid)?.let { db.schoolStageDao().delete(it) }
                    "good" -> db.goodItemDao().getByUuid(t.uuid)?.let { db.goodItemDao().delete(it) }
                    "custom_field" -> db.babyCustomFieldDao().getByUuid(t.uuid)?.let { db.babyCustomFieldDao().delete(it) }
                    "anniversary" -> db.anniversaryDao().getByUuid(t.uuid)?.let { db.anniversaryDao().delete(it) }
                    "vaccine" -> db.vaccinationDao().getByUuid(t.uuid)?.let { db.vaccinationDao().delete(it) }
                }
            }

            val active = changes.filter { !it.deleted }
            if (active.isEmpty()) return@withTransaction

            // 2) baby：建立 uuid → 本机 id 映射（avatarPath 是本机文件路径，保留本机值不参与合并）
            val babyUuidToId = mutableMapOf<String, Long>()
            active.filter { it.table == "baby" }.forEach { change ->
                val b = SyncJson.babyFromJson(JSONObject(change.json))
                val local = db.babyDao().getByUuid(b.uuid)
                val row = if (local != null) b.copy(id = local.id, avatarPath = local.avatarPath) else b
                db.babyDao().upsertAll(listOf(row))
                babyUuidToId[b.uuid] = row.id
            }

            // 3) record 第一遍：插入/更新，收集 uuid → id 映射与待回填的 parentUuid
            val recordUuidToId = mutableMapOf<String, Long>()
            val recordWithParent = mutableListOf<Pair<RecordEntity, String>>()
            active.filter { it.table == "record" }.forEach { change ->
                val o = JSONObject(change.json)
                val babyId = babyUuidToId[o.optString("babyUuid")] ?: return@forEach
                val r = SyncJson.recordFromJson(o)
                val local = db.recordDao().getByUuid(r.uuid)
                val row = (if (local != null) r.copy(id = local.id) else r).copy(babyId = babyId)
                db.recordDao().upsertAll(listOf(row))
                recordUuidToId[row.uuid] = row.id
                val parentUuid = o.optString("parentUuid").takeIf {
                    o.has("parentUuid") && !o.isNull("parentUuid") && it.isNotEmpty()
                }
                if (parentUuid != null) recordWithParent.add(row to parentUuid)
            }

            // 4) record 第二遍：回填 parentId
            recordWithParent.forEach { (row, parentUuid) ->
                val parentId = recordUuidToId[parentUuid]
                if (parentId != null) {
                    db.recordDao().update(row.copy(parentId = parentId))
                }
            }

            // 5) 其余子表
            suspend fun upsertChild(table: String) {
                active.filter { it.table == table }.forEach { change ->
                    val o = JSONObject(change.json)
                    val babyId = babyUuidToId[o.optString("babyUuid")] ?: return@forEach
                    when (table) {
                        "todo" -> {
                            val parsed = SyncJson.todoFromJson(o)
                            val row = parsed.copy(
                                id = db.todoDao().getByUuid(parsed.uuid)?.id ?: 0L,
                                babyId = babyId
                            )
                            db.todoDao().upsertAll(listOf(row))
                        }
                        "school" -> {
                            val parsed = SyncJson.schoolFromJson(o)
                            val row = parsed.copy(
                                id = db.schoolStageDao().getByUuid(parsed.uuid)?.id ?: 0L,
                                babyId = babyId
                            )
                            db.schoolStageDao().upsertAll(listOf(row))
                        }
                        "good" -> {
                            val parsed = SyncJson.goodFromJson(o)
                            val row = parsed.copy(
                                id = db.goodItemDao().getByUuid(parsed.uuid)?.id ?: 0L,
                                babyId = babyId
                            )
                            db.goodItemDao().upsertAll(listOf(row))
                        }
                        "custom_field" -> {
                            val parsed = SyncJson.customFieldFromJson(o)
                            val row = parsed.copy(
                                id = db.babyCustomFieldDao().getByUuid(parsed.uuid)?.id ?: 0L,
                                babyId = babyId
                            )
                            db.babyCustomFieldDao().upsertAll(listOf(row))
                        }
                        "anniversary" -> {
                            val parsed = SyncJson.anniversaryFromJson(o)
                            val row = parsed.copy(
                                id = db.anniversaryDao().getByUuid(parsed.uuid)?.id ?: 0L,
                                babyId = babyId
                            )
                            db.anniversaryDao().upsertAll(listOf(row))
                        }
                        "vaccine" -> {
                            val parsed = SyncJson.vaccineFromJson(o)
                            val row = parsed.copy(
                                id = db.vaccinationDao().getByUuid(parsed.uuid)?.id ?: 0L,
                                babyId = babyId
                            )
                            db.vaccinationDao().upsertAll(listOf(row))
                        }
                    }
                }
            }
            upsertChild("todo")
            upsertChild("school")
            upsertChild("good")
            upsertChild("custom_field")
            upsertChild("anniversary")
            upsertChild("vaccine")
        }
    }

    /**
     * 同步成功后清除"对端已确认收到"的墓碑，防止墓碑无限累积。
     * 限制：本 App 定位两台设备互同步；若新增第三台设备且其数据落后于清墓碑时刻，
     * 被清掉的删除记录可能在其上复活。两台机场景无此问题。
     */
    suspend fun clearTombstonesConfirmed(changes: List<SyncChange>) {
        val db = AppGraph.database
        changes.filter { it.deleted }.forEach { t ->
            db.syncTombstoneDao().delete(t.table, t.uuid)
        }
    }
}