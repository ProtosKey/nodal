package nodal.app.domain.solver

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import nodal.app.domain.basic.CanSolve
import nodal.app.domain.math.DifferenceEngine
import nodal.app.domain.model.Coordinates
import nodal.app.domain.model.Function

object NewtonFiniteForwardSolver : CanSolve {
    override fun solve(points: Coordinates, count: Long, point: BigDecimal): Function {
        val x = points.map { it.x }
        val y = points.map { it.y }
        val h = DifferenceEngine.verifyEquidistant(x, count)
        val table = DifferenceEngine.computeTable(y)
        return Function.NewtonFinite(x.first(), h, table, forward = true)
    }
}

