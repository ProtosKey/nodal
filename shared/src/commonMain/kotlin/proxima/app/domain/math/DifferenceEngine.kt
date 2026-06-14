package proxima.app.domain.math

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import proxima.app.domain.exception.InitException

object DifferenceEngine {
    fun computeTable(y: List<BigDecimal>): List<List<BigDecimal>> {
        val n = y.size
        val table = mutableListOf<MutableList<BigDecimal>>()
        table.add(y.toMutableList())
        for (k in 1 until n) {
            val row = mutableListOf<BigDecimal>()
            for (i in 0 until n - k) {
                row.add(table[k - 1][i + 1] - table[k - 1][i])
            }
            table.add(row)
        }
        return table
    }

    fun verifyEquidistant(x: List<BigDecimal>, count: Long): BigDecimal {
        if (x.size < 2) throw InitException("Нужно минимум 2 точки")
        val mode = DecimalUtils.getMode(count)
        val h = x[1] - x[0]
        for (i in 1 until x.size - 1) {
            val diff = (x[i + 1] - x[i]).abs()
            val expected = h.abs()
            val eps = BigDecimal.ONE.divide(BigDecimal.fromLong(1_000_000L), mode)
            if ((diff - expected).abs() > eps) {
                throw InitException("Узлы не равноотстоящие — методы Ньютона и Гаусса требуют равного шага")
            }
        }
        return h
    }
}
