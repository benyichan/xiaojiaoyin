package com.xiaojiaoyin.baby.domain

import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.data.db.entity.TAG_KEY_MOMENT
import com.xiaojiaoyin.baby.data.db.entity.tagsList
import com.xiaojiaoyin.baby.data.db.entity.withTags
import com.xiaojiaoyin.baby.sync.SyncJson
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoTagsTest {

    private fun photo(
        id: Long,
        tags: String = "",
        parentId: Long? = null
    ) = RecordEntity(
        id = id,
        babyId = 1,
        type = RecordType.PHOTO,
        occurredAt = 1000,
        detailJson = """{"path":"photos/a.jpg"}""",
        note = "",
        tags = tags,
        parentId = parentId,
        createdAt = 1000
    )

    @Test
    fun `标签解析忽略空项并去空白`() {
        val r = photo(1, "关键时刻, 满月 ,,")
        assertEquals(listOf("关键时刻", "满月"), r.tagsList())
    }

    @Test
    fun `withTags 去重并去除空白`() {
        val r = photo(1).withTags(listOf("关键时刻", "关键时刻", " 满月 "))
        assertEquals("关键时刻,满月", r.tags)
    }

    @Test
    fun `节点配图自动带关键时刻标签`() {
        val r = photo(1, parentId = 9)
        val withMoment = r.withTags(r.tagsList() + TAG_KEY_MOMENT)
        assertTrue(withMoment.tagsList().contains(TAG_KEY_MOMENT))
    }

    @Test
    fun `同步序列化保留标签与父节点`() {
        val r = photo(7, tags = "关键时刻,满月", parentId = 9)
        val restored = SyncJson.recordFromJson(JSONObject(SyncJson.recordToJson(r)))
        assertEquals("关键时刻,满月", restored.tags)
        assertEquals(9L, restored.parentId)
    }

    @Test
    fun `同步序列化无父节点时保留空`() {
        val r = photo(8)
        val restored = SyncJson.recordFromJson(JSONObject(SyncJson.recordToJson(r)))
        assertNull(restored.parentId)
    }
}
