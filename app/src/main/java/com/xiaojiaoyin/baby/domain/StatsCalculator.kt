package com.xiaojiaoyin.baby.domain

import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import java.time.Instant
import java.time.ZoneId

data class MonthlyOverview(
    val total: Int,
    val feeding: Int,
    val crying: Int,
    val growth: Int,
    val nodes: Int
)

object StatsCalculator {

    fun monthlyOverview(records: List<RecordEntity>, now: Long, zone: ZoneId = ZoneId.of("Asia/Shanghai")): MonthlyOverview {
        val month = Instant.ofEpochMilli(now).atZone(zone).monthValue
        val thisMonth = records.filter {
            Instant.ofEpochMilli(it.occurredAt).atZone(zone).monthValue == month
        }
        return MonthlyOverview(
            total = thisMonth.size,
            feeding = thisMonth.count { it.type == RecordType.FEEDING },
            crying = thisMonth.count { it.type == RecordType.CRYING },
            growth = thisMonth.count { it.type == RecordType.GROWTH },
            nodes = thisMonth.count { it.type == RecordType.NODE }
        )
    }

    fun feedingTypeRatio(records: List<RecordEntity>): Map<String, Int> {
        val result = linkedMapOf("母乳" to 0, "奶粉" to 0, "辅食" to 0)
        records.filter { it.type == RecordType.FEEDING }.forEach { r ->
            val kind = runCatching {
                Regex("\"kind\"\\s*:\\s*\"([^\"]+)\"").find(r.detailJson)
                    ?.groupValues?.getOrNull(1) ?: "母乳"
            }
                .getOrDefault("母乳")
            result[kind] = (result[kind] ?: 0) + 1
        }
        return result
    }

    /** 24 小时按 3 小时分 8 桶，返回每桶记录数（index 0 = 0-2 点） */
    fun cryingTimeBuckets(records: List<RecordEntity>, zone: ZoneId = ZoneId.of("Asia/Shanghai")): List<Int> {
        val buckets = MutableList(8) { 0 }
        records.filter { it.type == RecordType.CRYING }.forEach { r ->
            val hour = Instant.ofEpochMilli(r.occurredAt).atZone(zone).hour
            buckets[hour / 3] += 1
        }
        return buckets
    }

    fun todoCompletion(todos: List<TodoEntity>): Pair<Int, Int> {
        val done = todos.count { it.completed }
        return done to todos.size
    }
}
