package com.xiaojiaoyin.baby.sync

import com.xiaojiaoyin.baby.data.AppGraph
import org.json.JSONObject

class SyncManager {

    /** 提取本机全部数据为变更集 */
    suspend fun extractChanges(): List<SyncChange> {
        val db = AppGraph.database
        return buildList {
            db.babyDao().getAll().forEach {
                add(SyncChange("baby", it.id, it.createdAt, SyncJson.babyToJson(it)))
            }
            db.recordDao().getAllAll().forEach {
                add(SyncChange("record", it.id, it.updatedAt, SyncJson.recordToJson(it)))
            }
            db.todoDao().getAllAll().forEach {
                add(SyncChange("todo", it.id, it.createdAt, SyncJson.todoToJson(it)))
            }
            db.schoolStageDao().getAllAll().forEach {
                add(SyncChange("school", it.id, it.createdAt, SyncJson.schoolToJson(it)))
            }
            db.goodItemDao().getAllAll().forEach {
                add(SyncChange("good", it.id, it.createdAt, SyncJson.goodToJson(it)))
            }
            db.babyCustomFieldDao().getAllAll().forEach {
                add(SyncChange("custom_field", it.id, it.createdAt, SyncJson.customFieldToJson(it)))
            }
        }
    }

    /** 应用合并后的变更集到本机 */
    suspend fun applyChanges(changes: List<SyncChange>) {
        val db = AppGraph.database
        changes.groupBy { it.table }.forEach { (table, list) ->
            when (table) {
                "baby" -> db.babyDao().upsertAll(list.map { SyncJson.babyFromJson(JSONObject(it.json)) })
                "record" -> db.recordDao().upsertAll(list.map { SyncJson.recordFromJson(JSONObject(it.json)) })
                "todo" -> db.todoDao().upsertAll(list.map { SyncJson.todoFromJson(JSONObject(it.json)) })
                "school" -> db.schoolStageDao().upsertAll(list.map { SyncJson.schoolFromJson(JSONObject(it.json)) })
                "good" -> db.goodItemDao().upsertAll(list.map { SyncJson.goodFromJson(JSONObject(it.json)) })
                "custom_field" -> db.babyCustomFieldDao().upsertAll(
                    list.map { SyncJson.customFieldFromJson(JSONObject(it.json)) }
                )
            }
        }
    }
}
