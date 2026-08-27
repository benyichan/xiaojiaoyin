package com.xiaojiaoyin.baby.sync

import org.json.JSONObject

data class SyncChange(
    val table: String,
    val uuid: String,
    val updatedAt: Long,
    val deleted: Boolean = false,
    val json: String
)

/**
 * 离线同步合并规则（v2）：
 * - 合并键 = (table, uuid)，uuid 跨设备稳定，自增 id 不参与
 * - LWW：updatedAt 较新者胜
 * - 同 updatedAt 时按 uuid 字典序取大者（确定性 tie-break，两端收敛）
 * - 墓碑（deleted=true）与普通变更同规则竞争，胜出即表示该记录已被删除
 */
object SyncMerger {

    private const val FORMAT_VERSION = "V2"

    fun mergeChanges(local: List<SyncChange>, remote: List<SyncChange>): List<SyncChange> {
        val merged = linkedMapOf<String, SyncChange>()
        (local + remote).forEach { change ->
            val existing = merged[change.key()]
            if (existing == null || change.winsOver(existing)) {
                merged[change.key()] = change
            }
        }
        return merged.values.toList()
    }

    private fun SyncChange.winsOver(other: SyncChange): Boolean {
        if (updatedAt != other.updatedAt) return updatedAt > other.updatedAt
        if (uuid != other.uuid) return uuid > other.uuid
        return json > other.json
    }

    fun bundleToJson(changes: List<SyncChange>): String {
        return buildString {
            appendLine(FORMAT_VERSION)
            changes.forEach {
                // json 内联在行尾，最后一段不 split；含换行符的 json 会被截断，
                // 因此序列化前保证 json 为单行（JSONObject.toString() 不带换行）
                append("${it.table}|${it.uuid}|${it.updatedAt}|${if (it.deleted) "D" else "A"}|${it.json}\n")
            }
        }
    }

    fun bundleFromJson(text: String): List<SyncChange> {
        val lines = text.lineSequence().filter { it.isNotBlank() }.toList()
        if (lines.isEmpty()) return emptyList()
        if (lines.first() != FORMAT_VERSION) {
            throw IllegalArgumentException("不支持的同步文件格式（需要 v2，旧版导出文件请删除后重新导出）")
        }
        return lines.drop(1).mapIndexed { idx, line ->
            val parts = line.split("|", limit = 5)
            if (parts.size < 5) {
                throw IllegalArgumentException("同步数据第 ${idx + 2} 行格式损坏")
            }
            SyncChange(
                table = parts[0],
                uuid = parts[1],
                updatedAt = parts[2].toLong(),
                deleted = parts[3] == "D",
                json = parts[4]
            )
        }
    }

    private fun SyncChange.key(): String = "$table:$uuid"
}