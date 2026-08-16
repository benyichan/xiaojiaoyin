package com.xiaojiaoyin.baby.domain

import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class StatsCalculatorTest {

    private fun record(type: RecordType, occurredAt: Long, detail: String = "{}") =
        RecordEntity(id = 0, babyId = 1, type = type, occurredAt = occurredAt, detailJson = detail, createdAt = 0)

    @Test
    fun `月度概览统计各类型`() {
        val now = 1_750_000_000_000L
        val records = listOf(
            record(RecordType.FEEDING, now),
            record(RecordType.FEEDING, now),
            record(RecordType.CRYING, now),
            record(RecordType.GROWTH, now),
            record(RecordType.NODE, now),
            record(RecordType.FEEDING, now - 40L * 24 * 3600 * 1000)
        )
        val overview = StatsCalculator.monthlyOverview(records, now)
        assertEquals(5, overview.total)
        assertEquals(2, overview.feeding)
        assertEquals(1, overview.crying)
        assertEquals(1, overview.growth)
        assertEquals(1, overview.nodes)
    }

    @Test
    fun `喂养类型占比`() {
        val records = listOf(
            record(RecordType.FEEDING, 0, """{"kind":"母乳"}"""),
            record(RecordType.FEEDING, 0, """{"kind":"奶粉"}"""),
            record(RecordType.FEEDING, 0, """{"kind":"奶粉"}"""),
            record(RecordType.CRYING, 0)
        )
        val ratio = StatsCalculator.feedingTypeRatio(records)
        assertEquals(1, ratio["母乳"])
        assertEquals(2, ratio["奶粉"])
        assertEquals(0, ratio["辅食"])
    }

    @Test
    fun `哭闹时段分桶`() {
        val records = listOf(
            record(RecordType.CRYING, 1_750_000_000_000L), // 某时刻
            record(RecordType.CRYING, 1_750_000_000_000L),
            record(RecordType.FEEDING, 1_750_000_000_000L)
        )
        val buckets = StatsCalculator.cryingTimeBuckets(records)
        assertEquals(2, buckets.sum())
        assertEquals(8, buckets.size)
    }

    @Test
    fun `待办完成率`() {
        val todos = listOf(
            TodoEntity(id = 1, babyId = 1, title = "a", timeAt = 0, completed = true, createdAt = 0),
            TodoEntity(id = 2, babyId = 1, title = "b", timeAt = 0, completed = false, createdAt = 0),
            TodoEntity(id = 3, babyId = 1, title = "c", timeAt = 0, completed = true, createdAt = 0)
        )
        val (done, total) = StatsCalculator.todoCompletion(todos)
        assertEquals(2, done)
        assertEquals(3, total)
    }
}
