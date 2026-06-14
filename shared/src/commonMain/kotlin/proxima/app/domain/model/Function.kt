package proxima.app.domain.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import proxima.app.domain.basic.CanVisit
import proxima.app.domain.basic.FunctionVisitor
import proxima.app.domain.math.DecimalUtils

sealed class Function : CanVisit {
    abstract fun calculate(value: BigDecimal, count: Long): BigDecimal

    open fun warningAt(value: BigDecimal, count: Long): String? = null

    class Lagrange(
        val xNodes: List<BigDecimal>,
        val yNodes: List<BigDecimal>
    ) : Function() {
        override fun calculate(value: BigDecimal, count: Long): BigDecimal {
            val mode = DecimalUtils.getMode(count)
            var result = BigDecimal.ZERO
            for (i in xNodes.indices) {
                var term = yNodes[i]
                for (j in xNodes.indices) {
                    if (j != i) {
                        term = (term * (value - xNodes[j])).divide(xNodes[i] - xNodes[j], mode)
                    }
                }
                result += term
            }
            return result
        }

        override fun <R> acceptVisitor(visitor: FunctionVisitor<R>): R =
            visitor.visitLagrange(xNodes, yNodes)
    }

    class NewtonFinite(
        val x0: BigDecimal,
        val h: BigDecimal,
        val difTable: List<List<BigDecimal>>,
        val forward: Boolean
    ) : Function() {
        override fun calculate(value: BigDecimal, count: Long): BigDecimal {
            val mode = DecimalUtils.getMode(count)
            val t = (value - x0).divide(h, mode)

            val n = difTable[0].size - 1
            var result = BigDecimal.ZERO
            var cf = BigDecimal.ONE

            for (k in difTable.indices) {
                val tableIndex = if (forward) 0 else (n - k).coerceAtLeast(0)
                if (tableIndex >= difTable[k].size) break
                result += cf * difTable[k][tableIndex]
                val newFactor = if (forward) {
                    t - BigDecimal.fromLong(k.toLong())
                } else {
                    t + BigDecimal.fromLong(k.toLong())
                }
                cf = (cf * newFactor).divide(BigDecimal.fromLong((k + 1).toLong()), mode)
            }
            return result
        }

        override fun warningAt(value: BigDecimal, count: Long): String? {
            val mode = DecimalUtils.getMode(count)
            val t = (value - x0).divide(h, mode)
            val zero = BigDecimal.ZERO
            val one = BigDecimal.ONE
            return if (forward) {
                if (t !in zero..one)
                    "Формула рассчитана на интерполяцию в начале таблицы"
                else null
            } else {
                if (t > zero || t < BigDecimal.fromInt(-1))
                    "Формула рассчитана на интерполяцию в конце таблицы"
                else null
            }
        }

        override fun <R> acceptVisitor(visitor: FunctionVisitor<R>): R =
            visitor.visitNewtonFinite(x0, h, difTable, forward)
    }

    class Gauss(
        val xMid: BigDecimal,
        val h: BigDecimal,
        val difTable: List<List<BigDecimal>>,
        val midIndex: Int,
        val forward: Boolean
    ) : Function() {
        override fun calculate(value: BigDecimal, count: Long): BigDecimal {
            val mode = DecimalUtils.getMode(count)
            val t = (value - xMid).divide(h, mode)

            val m = midIndex
            var result =
                if (difTable.isNotEmpty() && m < difTable[0].size) difTable[0][m] else BigDecimal.ZERO
            var cf = BigDecimal.ONE

            for (k in 1 until difTable.size) {
                val newFactor = if (forward) {
                    if (k % 2 == 0) {
                        t - BigDecimal.fromLong((k / 2).toLong())
                    } else {
                        t + BigDecimal.fromLong(((k - 1) / 2).toLong())
                    }
                } else {
                    if (k % 2 == 0) {
                        t + BigDecimal.fromLong((k / 2).toLong())
                    } else {
                        t - BigDecimal.fromLong(((k - 1) / 2).toLong())
                    }
                }
                cf = (cf * newFactor).divide(BigDecimal.fromLong(k.toLong()), mode)

                val tableIndex = if (forward) m - k / 2 else m - (k + 1) / 2
                if (tableIndex < 0 || tableIndex >= difTable[k].size) break
                result += cf * difTable[k][tableIndex]
            }
            return result
        }

        override fun warningAt(value: BigDecimal, count: Long): String? {
            val mode = DecimalUtils.getMode(count)
            val t = (value - xMid).divide(h, mode)
            val zero = BigDecimal.ZERO
            val one = BigDecimal.ONE
            return if (forward) {
                if (t !in zero..one)
                    "Формула оптимальна в одном шаге справа от центрального узла"
                else null
            } else {
                if (t > zero || t < BigDecimal.fromInt(-1))
                    "Формула оптимальна в одном шаге слева от центрального узла"
                else null
            }
        }

        override fun <R> acceptVisitor(visitor: FunctionVisitor<R>): R =
            visitor.visitGauss(xMid, h, difTable, midIndex, forward)
    }

    class Stirling(
        val xMid: BigDecimal,
        val h: BigDecimal,
        val difTable: List<List<BigDecimal>>,
        val midIndex: Int
    ) : Function() {
        override fun calculate(value: BigDecimal, count: Long): BigDecimal {
            val mode = DecimalUtils.getMode(count)
            val t = (value - xMid).divide(h, mode)

            val m = midIndex
            val two = BigDecimal.fromInt(2)
            if (difTable.isEmpty() || m >= difTable[0].size) return BigDecimal.ZERO
            var result = difTable[0][m]
            var product = BigDecimal.ONE
            var factorial = BigDecimal.ONE
            val tSquared = t * t
            var j = 1
            while (true) {
                val oddK = 2 * j - 1
                val evenK = 2 * j
                if (oddK >= difTable.size) break
                factorial *= BigDecimal.fromLong(oddK.toLong())
                val oddIdx1 = m - j
                val oddIdx2 = m - j + 1
                if (oddIdx1 < 0 || oddIdx2 >= difTable[oddK].size) break
                val avgOdd = (difTable[oddK][oddIdx1] + difTable[oddK][oddIdx2]).divide(two, mode)
                result += (t * product).divide(factorial, mode) * avgOdd
                if (evenK >= difTable.size) break
                factorial *= BigDecimal.fromLong(evenK.toLong())
                val evenIdx = m - j
                if (evenIdx < 0 || evenIdx >= difTable[evenK].size) break
                result += (tSquared * product).divide(factorial, mode) * difTable[evenK][evenIdx]
                product *= (tSquared - BigDecimal.fromLong((j * j).toLong()))
                j++
            }
            return result
        }

        override fun warningAt(value: BigDecimal, count: Long): String? {
            val mode = DecimalUtils.getMode(count)
            val t = (value - xMid).divide(h, mode)
            val half = BigDecimal.fromDouble(0.5)
            return if (t.abs() > half)
                "Формула оптимальна около центрального узла"
            else null
        }

        override fun <R> acceptVisitor(visitor: FunctionVisitor<R>): R =
            visitor.visitStirling(xMid, h, difTable, midIndex)
    }

    class Bessel(
        val x0: BigDecimal,
        val h: BigDecimal,
        val difTable: List<List<BigDecimal>>,
        val midIndex: Int
    ) : Function() {
        override fun calculate(value: BigDecimal, count: Long): BigDecimal {
            val mode = DecimalUtils.getMode(count)
            val t = (value - x0).divide(h, mode)
            val m = midIndex
            val two = BigDecimal.fromInt(2)
            if (difTable.isEmpty() || m >= difTable[0].size - 1) return BigDecimal.ZERO
            var result = (difTable[0][m] + difTable[0][m + 1]).divide(two, mode)
            var omega = BigDecimal.ONE
            var factorial = BigDecimal.ONE
            val tMinusHalf = t - BigDecimal.fromDouble(0.5)
            var j = 0
            while (true) {
                val oddK = 2 * j + 1
                if (oddK >= difTable.size) break
                factorial *= BigDecimal.fromLong(oddK.toLong())
                val oddIdx = m - j
                if (oddIdx < 0 || oddIdx >= difTable[oddK].size) break
                result += (tMinusHalf * omega).divide(factorial, mode) * difTable[oddK][oddIdx]
                omega = omega * (t + BigDecimal.fromLong(j.toLong())) * (t - BigDecimal.fromLong((j + 1).toLong()))

                val evenK = 2 * j + 2
                if (evenK >= difTable.size) break
                factorial *= BigDecimal.fromLong(evenK.toLong())
                val evenIdx1 = m - j - 1
                val evenIdx2 = m - j
                if (evenIdx1 < 0 || evenIdx2 >= difTable[evenK].size) break
                val avgEven = (difTable[evenK][evenIdx1] + difTable[evenK][evenIdx2]).divide(two, mode)
                result += omega.divide(factorial, mode) * avgEven
                j++
            }
            return result
        }

        override fun warningAt(value: BigDecimal, count: Long): String? {
            val mode = DecimalUtils.getMode(count)
            val t = (value - x0).divide(h, mode)
            val zero = BigDecimal.ZERO
            val one = BigDecimal.ONE
            return if (t !in zero..one)
                "Формула оптимальна между двумя центральными узлами таблицы"
            else null
        }

        override fun <R> acceptVisitor(visitor: FunctionVisitor<R>): R =
            visitor.visitBessel(x0, h, difTable, midIndex)
    }
}
