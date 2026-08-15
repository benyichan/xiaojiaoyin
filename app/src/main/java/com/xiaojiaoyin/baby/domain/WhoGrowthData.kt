package com.xiaojiaoyin.baby.domain

import android.content.Context

data class WhoPoint(
    val month: Int,
    val p3: Double,
    val p50: Double,
    val p97: Double
)

/**
 * WHO 儿童生长标准（0-60 月）离线数据：
 * - 身高：0-23 月用身长表（lfa），24-60 月用身高表（hfa）
 * - 体重：wfa（0-60 月）
 * 百分位取 SD2neg（≈P2.3）、SD0（P50）、SD2（≈P97.7）。
 */
class WhoGrowthData(context: Context) {
    private val maleHeight: Map<Int, WhoPoint> = loadHeight(context, isMale = true)
    private val femaleHeight: Map<Int, WhoPoint> = loadHeight(context, isMale = false)
    private val maleWeight: Map<Int, WhoPoint> = loadWeight(context, isMale = true)
    private val femaleWeight: Map<Int, WhoPoint> = loadWeight(context, isMale = false)

    fun heightPercentiles(isMale: Boolean, month: Int): WhoPoint? =
        (if (isMale) maleHeight else femaleHeight)[month]

    fun weightPercentiles(isMale: Boolean, month: Int): WhoPoint? =
        (if (isMale) maleWeight else femaleWeight)[month]

    private fun loadHeight(context: Context, isMale: Boolean): Map<Int, WhoPoint> {
        val sex = if (isMale) "boys" else "girls"
        val length = parseCsv(context, "growth/lfa_$sex.csv")
        val height = parseCsv(context, "growth/hfa_$sex.csv")
        return (length + height).associateBy { it.month }
    }

    private fun loadWeight(context: Context, isMale: Boolean): Map<Int, WhoPoint> {
        val sex = if (isMale) "boys" else "girls"
        return parseCsv(context, "growth/wfa_$sex.csv").associateBy { it.month }
    }

    private fun parseCsv(context: Context, asset: String): List<WhoPoint> {
        return context.assets.open(asset).bufferedReader().useLines { lines ->
            parseCsvLines(lines)
        }
    }

    companion object {
        internal fun parseCsvLines(lines: Sequence<String>): List<WhoPoint> {
            val iterator = lines.iterator()
            if (!iterator.hasNext()) return emptyList()
            val header = iterator.next().split(",").map { it.trim() }
            val iMonth = header.indexOf("Month")
            val iP3 = header.indexOf("SD2neg")
            val iP50 = header.indexOf("SD0")
            val iP97 = header.indexOfFirst { it == "SD2" }
            if (listOf(iMonth, iP3, iP50, iP97).any { it < 0 }) return emptyList()
            val maxIndex = maxOf(iMonth, iP3, iP50, iP97)
            val result = mutableListOf<WhoPoint>()
            while (iterator.hasNext()) {
                val line = iterator.next()
                val parts = line.split(",")
                if (parts.size <= maxIndex) continue
                val month = parts[iMonth].trim().toIntOrNull() ?: continue
                val p3 = parts[iP3].trim().toDoubleOrNull() ?: continue
                val p50 = parts[iP50].trim().toDoubleOrNull() ?: continue
                val p97 = parts[iP97].trim().toDoubleOrNull() ?: continue
                result.add(
                    WhoPoint(
                        month = month,
                        p3 = p3,
                        p50 = p50,
                        p97 = p97
                    )
                )
            }
            return result
        }
    }
}
