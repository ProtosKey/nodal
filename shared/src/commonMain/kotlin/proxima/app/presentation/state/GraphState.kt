package proxima.app.presentation.state

import proxima.app.data.model.FunctionType
import proxima.app.presentation.model.PointData

data class GraphState(
    val points: List<PointData> = emptyList(),
    val graph: Map<FunctionType, List<List<PointData>>> = emptyMap(),
    val canAdd: Boolean = false,
    val isLoading: Boolean = false,
    val interpolationPoint: PointData? = null,
)
