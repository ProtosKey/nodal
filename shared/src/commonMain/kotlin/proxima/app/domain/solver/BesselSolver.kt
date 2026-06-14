package proxima.app.domain.solver

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import proxima.app.domain.basic.CanSolve
import proxima.app.domain.math.DifferenceEngine
import proxima.app.domain.model.Coordinates
import proxima.app.domain.model.Function

object BesselSolver : CanSolve {
    override fun solve(points: Coordinates, count: Long, point: BigDecimal): Function {
        val x = points.map { it.x }
        val y = points.map { it.y }
        val h = DifferenceEngine.verifyEquidistant(x, count)
        val table = DifferenceEngine.computeTable(y)
        val rawIndex = x.indexOfFirst { it >= point }
        val targetIndex = if (rawIndex == -1) x.size - 1 else rawIndex
        val m = (targetIndex - 1).coerceIn(0, x.size - 2)
        return Function.Bessel(x[m], h, table, m)
    }
}
