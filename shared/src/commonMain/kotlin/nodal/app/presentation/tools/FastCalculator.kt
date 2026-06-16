package nodal.app.presentation.tools

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import nodal.app.domain.basic.FunctionVisitor
import nodal.app.domain.math.toDouble

object FastCalculator : FunctionVisitor<FastFunction> {
    override fun visitLagrange(xNodes: List<BigDecimal>, yNodes: List<BigDecimal>): FastFunction {
        val xD = xNodes.map { it.toDouble() }
        val yD = yNodes.map { it.toDouble() }
        return FastFunction { x ->
            var result = 0.0
            for (i in xD.indices) {
                var term = yD[i]
                for (j in xD.indices) {
                    if (j != i) term *= (x - xD[j]) / (xD[i] - xD[j])
                }
                result += term
            }
            result
        }
    }

    override fun visitNewtonFinite(
        x0: BigDecimal,
        h: BigDecimal,
        difTable: List<List<BigDecimal>>,
        forward: Boolean
    ): FastFunction {
        val x0D = x0.toDouble()
        val hD = h.toDouble()
        val n = difTable[0].size - 1
        val tableD = difTable.map { row -> row.map { it.toDouble() } }
        return FastFunction { x ->
            val t = (x - x0D) / hD
            var result = 0.0
            var coeff = 1.0
            for (k in tableD.indices) {
                val tableIndex = if (forward) 0 else (n - k).coerceAtLeast(0)
                if (tableIndex >= tableD[k].size) break
                result += coeff * tableD[k][tableIndex]
                val factor = if (forward) t - k.toDouble() else t + k.toDouble()
                coeff = coeff * factor / (k + 1).toDouble()
            }
            result
        }
    }

    override fun visitGauss(
        xMid: BigDecimal,
        h: BigDecimal,
        difTable: List<List<BigDecimal>>,
        midIndex: Int,
        forward: Boolean
    ): FastFunction {
        val xMidD = xMid.toDouble()
        val hD = h.toDouble()
        val m = midIndex
        val tableD = difTable.map { row -> row.map { it.toDouble() } }
        return FastFunction { x ->
            val t = (x - xMidD) / hD
            var result = if (tableD.isNotEmpty() && m < tableD[0].size) tableD[0][m] else 0.0
            var coeff = 1.0
            for (k in 1 until tableD.size) {
                val newFactor = if (forward) {
                    if (k % 2 == 0) t - (k / 2).toDouble() else t + ((k - 1) / 2).toDouble()
                } else {
                    if (k % 2 == 0) t + (k / 2).toDouble() else t - ((k - 1) / 2).toDouble()
                }
                coeff = coeff * newFactor / k.toDouble()
                val tableIndex = if (forward) m - k / 2 else m - (k + 1) / 2
                if (tableIndex < 0 || tableIndex >= tableD[k].size) break
                result += coeff * tableD[k][tableIndex]
            }
            result
        }
    }

    override fun visitStirling(
        xMid: BigDecimal,
        h: BigDecimal,
        difTable: List<List<BigDecimal>>,
        midIndex: Int
    ): FastFunction {
        val xMidD = xMid.toDouble()
        val hD = h.toDouble()
        val m = midIndex
        val tableD = difTable.map { row -> row.map { it.toDouble() } }
        return FastFunction { x ->
            val t = (x - xMidD) / hD
            if (tableD.isEmpty() || m >= tableD[0].size) return@FastFunction 0.0
            var result = tableD[0][m]
            var product = 1.0
            var factorial = 1.0
            val tSq = t * t
            var j = 1
            while (true) {
                val oddK = 2 * j - 1
                val evenK = 2 * j
                if (oddK >= tableD.size) break
                factorial *= oddK.toDouble()
                val oddIdx1 = m - j
                val oddIdx2 = m - j + 1
                if (oddIdx1 < 0 || oddIdx2 >= tableD[oddK].size) break
                result += t * product / factorial * (tableD[oddK][oddIdx1] + tableD[oddK][oddIdx2]) / 2.0
                if (evenK >= tableD.size) break
                factorial *= evenK.toDouble()
                val evenIdx = m - j
                if (evenIdx < 0 || evenIdx >= tableD[evenK].size) break
                result += tSq * product / factorial * tableD[evenK][evenIdx]
                product *= tSq - (j * j).toDouble()
                j++
            }
            result
        }
    }

    override fun visitBessel(
        x0: BigDecimal,
        h: BigDecimal,
        difTable: List<List<BigDecimal>>,
        midIndex: Int
    ): FastFunction {
        val x0D = x0.toDouble()
        val hD = h.toDouble()
        val m = midIndex
        val tableD = difTable.map { row -> row.map { it.toDouble() } }
        return FastFunction { x ->
            val t = (x - x0D) / hD
            if (tableD.isEmpty() || m >= tableD[0].size - 1) return@FastFunction 0.0
            var result = (tableD[0][m] + tableD[0][m + 1]) / 2.0
            var omega = 1.0
            var factorial = 1.0
            val tMH = t - 0.5
            var j = 0
            while (true) {
                val oddK = 2 * j + 1
                if (oddK >= tableD.size) break
                factorial *= oddK.toDouble()
                val oddIdx = m - j
                if (oddIdx < 0 || oddIdx >= tableD[oddK].size) break
                result += tMH * omega / factorial * tableD[oddK][oddIdx]
                omega *= (t + j.toDouble()) * (t - (j + 1).toDouble())
                val evenK = 2 * j + 2
                if (evenK >= tableD.size) break
                factorial *= evenK.toDouble()
                val evenIdx1 = m - j - 1
                val evenIdx2 = m - j
                if (evenIdx1 < 0 || evenIdx2 >= tableD[evenK].size) break
                result += omega / factorial * (tableD[evenK][evenIdx1] + tableD[evenK][evenIdx2]) / 2.0
                j++
            }
            result
        }
    }
}

fun interface FastFunction {
    fun calculate(value: Double): Double
}
