package nodal.app.presentation.state

import nodal.app.presentation.model.PointEntry

data class InputState(
    val input: List<PointEntry> = emptyList(),
    val canAdd: Boolean = true,
    val interpolateAt: String = ""
)
