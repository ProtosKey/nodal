package proxima.app.data.utils

import proxima.app.data.model.FunctionType
import proxima.app.data.model.MessageType
import proxima.app.domain.basic.CanSolve
import proxima.app.domain.solver.BesselSolver
import proxima.app.domain.solver.GaussBackwardSolver
import proxima.app.domain.solver.GaussForwardSolver
import proxima.app.domain.solver.LagrangeSolver
import proxima.app.domain.solver.NewtonFiniteBackwardSolver
import proxima.app.domain.solver.NewtonFiniteForwardSolver
import proxima.app.domain.solver.StirlingSolver

object Defaults {
    fun exception(): String = "Неожиданная ошибка"
    fun message(): String = "Начните работу..."
    fun messageType(): MessageType = MessageType.GOOD
    fun visible(): Boolean = false

    fun solvers(): Map<FunctionType, CanSolve> = mapOf(
        FunctionType.LAGRANGE to LagrangeSolver,
        FunctionType.NEWTON_FORWARD to NewtonFiniteForwardSolver,
        FunctionType.NEWTON_BACKWARD to NewtonFiniteBackwardSolver,
        FunctionType.GAUSS_FORWARD to GaussForwardSolver,
        FunctionType.GAUSS_BACKWARD to GaussBackwardSolver,
        FunctionType.STIRLING to StirlingSolver,
        FunctionType.BESSEL to BesselSolver
    )
}
