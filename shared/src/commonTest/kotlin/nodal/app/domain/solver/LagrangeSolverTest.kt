package nodal.app.domain.solver

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import nodal.app.domain.model.Coordinates
import nodal.app.domain.model.Point
import kotlin.test.Test
import kotlin.test.assertTrue

class LagrangeSolverTest {
    @Test
    fun testLagrangePassesThroughNodes() {
        val points = Coordinates(mutableListOf(
            Point(BigDecimal.fromDouble(0.5), BigDecimal.fromDouble(1.5320)),
            Point(BigDecimal.fromDouble(0.55), BigDecimal.fromDouble(2.5356)),
            Point(BigDecimal.fromDouble(0.60), BigDecimal.fromDouble(3.5406)),
            Point(BigDecimal.fromDouble(0.65), BigDecimal.fromDouble(4.5462)),
            Point(BigDecimal.fromDouble(0.70), BigDecimal.fromDouble(5.5504))
        ))
        val fn = LagrangeSolver.solve(points, 32L, points[0].x)
        for (p in points) {
            val y = fn.calculate(p.x, 32L)
            val diff = (y - p.y).abs()
            assertTrue(diff < BigDecimal.fromDouble(0.001), "Lagrange should pass through node at x=${p.x}")
        }
    }

    @Test
    fun testNewtonForwardInterpolation() {
        val points = Coordinates(mutableListOf(
            Point(BigDecimal.fromDouble(0.5), BigDecimal.fromDouble(1.5320)),
            Point(BigDecimal.fromDouble(0.55), BigDecimal.fromDouble(2.5356)),
            Point(BigDecimal.fromDouble(0.60), BigDecimal.fromDouble(3.5406)),
            Point(BigDecimal.fromDouble(0.65), BigDecimal.fromDouble(4.5462)),
            Point(BigDecimal.fromDouble(0.70), BigDecimal.fromDouble(5.5504))
        ))
        val fn = NewtonFiniteForwardSolver.solve(points, 32L, points[0].x)
        for (p in points) {
            val y = fn.calculate(p.x, 32L)
            val diff = (y - p.y).abs()
            assertTrue(diff < BigDecimal.fromDouble(0.01), "Newton forward should pass through node at x=${p.x}")
        }
    }
}
