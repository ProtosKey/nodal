package nodal.app.data.utils

import nodal.app.data.model.FunctionType
import nodal.app.data.model.MessageType
import nodal.app.domain.basic.CanSolve
import nodal.app.domain.solver.BesselSolver
import nodal.app.domain.solver.GaussBackwardSolver
import nodal.app.domain.solver.GaussForwardSolver
import nodal.app.domain.solver.LagrangeSolver
import nodal.app.domain.solver.NewtonFiniteBackwardSolver
import nodal.app.domain.solver.NewtonFiniteForwardSolver
import nodal.app.domain.solver.StirlingSolver

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
