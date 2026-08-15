package com.xiaojiaoyin.baby.sync

import org.json.JSONObject

data class SyncChange(
    val table: String,
    val id: Long,
    val updatedAt: Long,
    val json: String
) {
    fun toJson(): JSONObject = JSONObject()
        .put("table", table)
        .put("id", id)
        .put("updatedAt", updatedAt)
        .put("json", JSONObject(json))

    companion object {
        fun fromJson(obj: JSONObject): SyncChange = SyncChange(
            table = obj.getString("table"),
            id = obj.getLong("id"),
            updatedAt = obj.getLong("updatedAt"),
            json = obj.getJSONObject("json").toString()
        )
    }
}

/**
 * 离线同步合并规则：
 * - 按 (table, id) 分组，保留 updatedAt 较新的一条
 * - 同 updatedAt 时本地优先
 */
object SyncMerger {

    fun mergeChanges(local: List<SyncChange>, remote: List<SyncChange>): List<SyncChange> {
        val merged = linkedMapOf<String, SyncChange>()
        local.forEach { merged[it.key()] = it }
        remote.forEach { change ->
            val existing = merged[change.key()]
            if (existing == null || change.updatedAt > existing.updatedAt) {
                merged[change.key()] = change
            }
        }
        return merged.values.toList()
    }

    fun bundleToJson(changes: List<SyncChange>): String {
        return changes.joinToString("\n") { "${it.table}|${it.id}|${it.updatedAt}|${it.json}" }
    }

    fun bundleFromJson(text: String): List<SyncChange> {
        return text.lineSequence()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split("|", limit = 4)
                SyncChange(
                    table = parts[0],
                    id = parts[1].toLong(),
                    updatedAt = parts[2].toLong(),
                    json = parts.getOrElse(3) { "{}" }
                )
            }
            .toList()
    }

    private fun SyncChange.key(): String = "$table:$id"
}
