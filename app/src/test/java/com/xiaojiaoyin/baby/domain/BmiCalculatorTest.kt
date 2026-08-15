package com.xiaojiaoyin.baby.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class BmiCalculatorTest {

    @Test
    fun `身高62cm体重6-8kg_BMI约17-7`() {
        val bmi = BmiCalculator.bmi(weightKg = 6.8, heightCm = 62.0)
        assertEquals(17.7, bmi, 0.1)
        assertEquals("正常", BmiCalculator.evaluate(6.8, 62.0))
    }

    @Test
    fun `过轻与过重评估`() {
        assertEquals("偏低", BmiCalculator.evaluate(3.5, 52.0))
        assertEquals("偏高", BmiCalculator.evaluate(9.5, 58.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `非法输入抛异常`() {
        BmiCalculator.bmi(0.0, 62.0)
    }
}
