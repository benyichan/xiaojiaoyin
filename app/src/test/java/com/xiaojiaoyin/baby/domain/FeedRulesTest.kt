package com.xiaojiaoyin.baby.domain

import com.xiaojiaoyin.baby.data.db.entity.RecordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedRulesTest {

    @Test
    fun `喂养25个月非活跃_24个月活跃`() {
        assertFalse(FeedRules.isActive(RecordType.FEEDING, ageMonths = 25))
        assertTrue(FeedRules.isActive(RecordType.FEEDING, ageMonths = 24))
    }

    @Test
    fun `哭闹36个月边界`() {
        assertTrue(FeedRules.isActive(RecordType.CRYING, ageMonths = 35))
        assertTrue(FeedRules.isActive(RecordType.CRYING, ageMonths = 36))
        assertFalse(FeedRules.isActive(RecordType.CRYING, ageMonths = 37))
    }

    @Test
    fun `生长永远活跃`() {
        assertTrue(FeedRules.isActive(RecordType.GROWTH, ageMonths = 120))
    }

    @Test
    fun `60天无记录自动折叠_近期记录不折叠`() {
        val now = 1_000_000L * 1000
        val longAgo = now - 61L * 24 * 60 * 60 * 1000
        val recent = now - 10L * 24 * 60 * 60 * 1000
        assertTrue(FeedRules.shouldAutoHide(RecordType.FEEDING, 6, longAgo, now))
        assertFalse(FeedRules.shouldAutoHide(RecordType.FEEDING, 6, recent, now))
        // 生长类型不参与断更折叠
        assertFalse(FeedRules.shouldAutoHide(RecordType.GROWTH, 6, longAgo, now))
    }

    @Test
    fun `月龄计算`() {
        val birth = 1_600_000_000_000L // 2020-09-13 左右
        val now = 1_700_000_000_000L // 2023-11-14 左右
        val months = FeedRules.ageMonths(birth, now)
        assertTrue("月龄应约 38 个月，实际 $months", months in 36..40)
    }
}
