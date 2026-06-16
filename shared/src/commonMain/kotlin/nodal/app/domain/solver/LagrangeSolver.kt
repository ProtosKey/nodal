package nodal.app.domain.solver

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import nodal.app.domain.basic.CanSolve
import nodal.app.domain.model.Coordinates
import nodal.app.domain.model.Function

object LagrangeSolver : CanSolve {
    override fun solve(points: Coordinates, count: Long, point: BigDecimal): Function {
        val x = points.map { it.x }
        val y = points.map { it.y }
        return Function.Lagrange(x, y)
    }
}
