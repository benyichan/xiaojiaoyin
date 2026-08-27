package com.xiaojiaoyin.baby.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class SyncMergerTest {

    private fun change(table: String, uuid: String, updatedAt: Long, deleted: Boolean = false) =
        SyncChange(
            table = table, uuid = uuid, updatedAt = updatedAt, deleted = deleted,
            json = """{"uuid":"$uuid"}"""
        )

    @Test
    fun `本地与远程不同记录合并保留双方`() {
        val local = listOf(change("record", "u-1", 100))
        val remote = listOf(change("record", "u-2", 200))
        val merged = SyncMerger.mergeChanges(local, remote)
        assertEquals(2, merged.size)
        assertTrue(merged.any { it.uuid == "u-1" })
        assertTrue(merged.any { it.uuid == "u-2" })
    }

    @Test
    fun `同一条记录取更新时间较新者`() {
        val local = listOf(change("record", "u-1", 100))
        val remote = listOf(change("record", "u-1", 300))
        val merged = SyncMerger.mergeChanges(local, remote)
        assertEquals(1, merged.size)
        assertEquals(300, merged[0].updatedAt)
    }

    @Test
    fun `同时刻同记录按 json 字典序确定性收敛`() {
        val local = listOf(SyncChange("record", "u-1", 500, json = """{"v":"a"}"""))
        val remote = listOf(SyncChange("record", "u-1", 500, json = """{"v":"z"}"""))
        // 合并顺序无关：无论先后，结果一致且可复现
        val m1 = SyncMerger.mergeChanges(local, remote)
        val m2 = SyncMerger.mergeChanges(remote, local)
        assertEquals(m1[0].json, m2[0].json)
        assertEquals("""{"v":"z"}""", m1[0].json)
    }

    @Test
    fun `墓碑较旧则数据复活`() {
        val tombstone = listOf(change("todo", "u-1", 500, deleted = true))
        val newerData = listOf(change("todo", "u-1", 800))
        val merged = SyncMerger.mergeChanges(tombstone, newerData)
        assertEquals(1, merged.size)
        assertFalse(merged[0].deleted)
    }

    @Test
    fun `墓碑较新则删除胜出`() {
        val data = listOf(change("todo", "u-1", 500))
        val newerTombstone = listOf(change("todo", "u-1", 800, deleted = true))
        val merged = SyncMerger.mergeChanges(data, newerTombstone)
        assertEquals(1, merged.size)
        assertTrue(merged[0].deleted)
    }

    @Test
    fun `bundle 序列化往返一致`() {
        val changes = listOf(
            change("baby", "u-7", 1000),
            change("todo", "u-8", 2000, deleted = true)
        )
        val text = SyncMerger.bundleToJson(changes)
        val restored = SyncMerger.bundleFromJson(text)
        assertEquals(changes.size, restored.size)
        assertEquals("baby", restored[0].table)
        assertEquals("u-8", restored[1].uuid)
        assertTrue(restored[1].deleted)
    }

    @Test
    fun `旧版无版本头文件直接报错`() {
        assertThrows(IllegalArgumentException::class.java) {
            SyncMerger.bundleFromJson("record|u-1|100|A|{}")
        }
    }

    @Test
    fun `损坏行报错而非静默跳过`() {
        assertThrows(IllegalArgumentException::class.java) {
            SyncMerger.bundleFromJson("V2\nrecord|u-1|100")
        }
    }
}