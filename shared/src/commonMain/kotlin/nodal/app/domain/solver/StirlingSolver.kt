package nodal.app.domain.solver

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import nodal.app.domain.basic.CanSolve
import nodal.app.domain.math.DifferenceEngine
import nodal.app.domain.model.Coordinates
import nodal.app.domain.model.Function

object StirlingSolver : CanSolve {
    override fun solve(points: Coordinates, count: Long, point: BigDecimal): Function {
        val x = points.map { it.x }
        val y = points.map { it.y }
        val h = DifferenceEngine.verifyEquidistant(x, count)
        val table = DifferenceEngine.computeTable(y)
        val m = x.indices.minByOrNull { idx ->
            (x[idx] - point).abs()
        }?.coerceIn(0, x.size - 1) ?: (x.size / 2)
        return Function.Stirling(x[m], h, table, m)
    }
}
