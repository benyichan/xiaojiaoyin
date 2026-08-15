package com.xiaojiaoyin.baby.domain

object BmiCalculator {
    fun bmi(weightKg: Double, heightCm: Double): Double {
        require(weightKg > 0 && heightCm > 0) { "体重与身高必须为正数" }
        val heightM = heightCm / 100.0
        return weightKg / (heightM * heightM)
    }

    /**
     * M1 简化评估（正式 WHO 百分位曲线在 M3 引入）：
     * BMI < 14 偏低，14-18 正常，>18 偏高。
     */
    fun evaluate(weightKg: Double, heightCm: Double): String {
        val value = bmi(weightKg, heightCm)
        return when {
            value < 14.0 -> "偏低"
            value > 18.0 -> "偏高"
            else -> "正常"
        }
    }
}
