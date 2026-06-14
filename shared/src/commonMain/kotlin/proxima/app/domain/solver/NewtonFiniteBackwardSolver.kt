package proxima.app.domain.solver

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import proxima.app.domain.basic.CanSolve
import proxima.app.domain.math.DifferenceEngine
import proxima.app.domain.model.Coordinates
import proxima.app.domain.model.Function

object NewtonFiniteBackwardSolver : CanSolve {
    override fun solve(points: Coordinates, count: Long, point: BigDecimal): Function {
        val x = points.map { it.x }
        val y = points.map { it.y }
        val h = DifferenceEngine.verifyEquidistant(x, count)
        val table = DifferenceEngine.computeTable(y)

        return Function.NewtonFinite(x.last(), h, table, forward = false)
    }
}

