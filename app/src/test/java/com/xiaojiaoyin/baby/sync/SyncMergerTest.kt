package com.xiaojiaoyin.baby.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncMergerTest {

    private fun change(table: String, id: Long, updatedAt: Long) =
        SyncChange(table = table, id = id, updatedAt = updatedAt, json = """{"id":$id}""")

    @Test
    fun `本地与远程不同记录合并保留双方`() {
        val local = listOf(change("record", 1, 100))
        val remote = listOf(change("record", 2, 200))
        val merged = SyncMerger.mergeChanges(local, remote)
        assertEquals(2, merged.size)
        assertTrue(merged.any { it.id == 1L })
        assertTrue(merged.any { it.id == 2L })
    }

    @Test
    fun `同一条记录取更新时间较新者`() {
        val local = listOf(change("record", 1, 100))
        val remote = listOf(change("record", 1, 300))
        val merged = SyncMerger.mergeChanges(local, remote)
        assertEquals(1, merged.size)
        assertEquals(300, merged[0].updatedAt)
    }

    @Test
    fun `同时刻本地优先`() {
        val local = listOf(change("record", 1, 500))
        val remote = listOf(change("record", 1, 500))
        val merged = SyncMerger.mergeChanges(local, remote)
        assertEquals(500, merged[0].updatedAt)
        assertEquals(local[0].json, merged[0].json)
    }

    @Test
    fun `bundle 序列化往返一致`() {
        val changes = listOf(change("baby", 7, 1000), change("todo", 8, 2000))
        val text = SyncMerger.bundleToJson(changes)
        val restored = SyncMerger.bundleFromJson(text)
        assertEquals(changes.size, restored.size)
        assertEquals("baby", restored[0].table)
        assertEquals(8, restored[1].id)
    }
}
