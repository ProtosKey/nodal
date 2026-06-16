package nodal.app.presentation.state

import nodal.app.data.model.FunctionType
import nodal.app.presentation.model.PointData

data class GraphState(
    val points: List<PointData> = emptyList(),
    val graph: Map<FunctionType, List<List<PointData>>> = emptyMap(),
    val canAdd: Boolean = false,
    val isLoading: Boolean = false,
    val interpolationPoint: PointData? = null,
)
