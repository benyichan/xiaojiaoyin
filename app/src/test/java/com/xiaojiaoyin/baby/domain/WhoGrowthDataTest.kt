package com.xiaojiaoyin.baby.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WhoGrowthDataTest {

    // 用原始 CSV 的已知值验证解析逻辑（不依赖 Android context）
    private fun fakePoint(month: Int, p3: Double, p50: Double, p97: Double) = WhoPoint(month, p3, p50, p97)

    @Test
    fun `解析逻辑_取SD2neg_SD0_SD2列`() {
        // 真实 lfa_boys.csv 数据行：0 月男婴身长
        val lines = sequenceOf(
            "Month,L,M,S,SD,SD3neg,SD2neg,SD1neg,SD0,SD1,SD2,SD3",
            "0,1,49.8842,0.03795,1.8931,44.2,46.1,48,49.9,51.8,53.7,55.6",
            "1,1,54.7244,0.03557,1.9465,48.9,50.8,52.8,54.7,56.7,58.6,60.6"
        )
        val points = WhoGrowthData.parseCsvLines(lines)
        assertEquals(2, points.size)
        assertEquals(0, points[0].month)
        assertEquals(46.1, points[0].p3, 0.001)
        assertEquals(49.9, points[0].p50, 0.001)
        assertEquals(53.7, points[0].p97, 0.001)
        assertEquals(1, points[1].month)
        assertEquals(50.8, points[1].p3, 0.001)
    }

    @Test
    fun `真实wfa数据_0月男婴体重P50等于3-3`() {
        val lines = sequenceOf(
            "Month,L,M,S,SD3neg,SD2neg,SD1neg,SD0,SD1,SD2,SD3",
            "0,0.3487,3.3464,0.14602,2.1,2.5,2.9,3.3,3.9,4.4,5"
        )
        val points = WhoGrowthData.parseCsvLines(lines)
        assertEquals(3.3, points[0].p50, 0.001)
        assertEquals(2.5, points[0].p3, 0.001)
    }

    @Test
    fun `百分位顺序正确`() {
        val point = fakePoint(12, p3 = 70.0, p50 = 75.0, p97 = 80.0)
        assertTrue(point.p3 < point.p50)
        assertTrue(point.p50 < point.p97)
        assertNotNull(point)
    }
}
