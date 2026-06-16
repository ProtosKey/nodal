package nodal.app.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import nodal.app.data.MainStore
import nodal.app.data.model.FunctionResult
import nodal.app.data.model.FunctionType
import nodal.app.data.model.MessageType
import nodal.app.domain.model.Coordinates
import nodal.app.domain.model.Function
import nodal.app.presentation.basic.BaseViewModel
import nodal.app.presentation.mapper.DataMapper
import nodal.app.presentation.mapper.RawMapper
import nodal.app.presentation.model.PointData
import nodal.app.presentation.state.GraphState
import nodal.app.presentation.tools.FastCalculator
import nodal.app.presentation.tools.StringParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GraphViewModel(store: MainStore) : BaseViewModel(store) {
    private var curvePoints: Int = 0
    private var curveJob: Job? = null
    private var currentLeft: Float = -10f
    private var currentRight: Float = 10f

    private companion object {
        const val Y_CLIP: Float = 1e6f
    }
    private val _graphState = MutableStateFlow(GraphState())
    val graphState = _graphState.asStateFlow()
    val notification = store.notification

    init {
        store.settings.onEach { settings ->
            curvePoints = settings.graphResolution.value.toInt()
        }.launchIn(viewModelScope)

        combine(
            store.rawPoints,
            store.isLoading,
            store.results,
            store.hiddenMethods
        ) { rawPoints, isLoading, _, _ ->
            val pointData = rawPoints.mapNotNull { raw ->
                runCatching { DataMapper.mapTo(RawMapper.toPoint(raw)) }.getOrNull()
            }
            _graphState.update {
                it.copy(
                    points = pointData,
                    canAdd = rawPoints.size < Coordinates.MAX_SIZE,
                    isLoading = isLoading,
                )
            }
            scheduleCurveRebuild()
        }.launchIn(viewModelScope)

        combine(
            store.interpolateAt,
            store.results,
        ) { _, _ ->
            _graphState.update { it.copy(interpolationPoint = computeInterpolationMarker()) }
        }.launchIn(viewModelScope)
    }

    private fun computeInterpolationMarker(): PointData? {
        val xBd = runCatching {
            StringParser.parseBigDecimal(store.interpolateAt.value)
        }.getOrNull() ?: return null
        val xD = xBd.doubleValue(false)
        val function = (store.results.value[FunctionType.LAGRANGE] as? FunctionResult.Success)
            ?.function
            ?: store.results.value.values
                .filterIsInstance<FunctionResult.Success>()
                .firstOrNull()
                ?.function
            ?: return null
        val fast = function.acceptVisitor(FastCalculator)
        val yD = runCatching { fast.calculate(xD) }.getOrNull() ?: return null
        if (yD.isNaN() || yD.isInfinite()) return null
        return PointData(xD.toFloat(), yD.toFloat())
    }

    fun updateVisible(left: Float, right: Float) {
        currentLeft = left
        currentRight = right
        scheduleCurveRebuild()
    }

    private fun scheduleCurveRebuild() {
        curveJob?.cancel()
        val left = currentLeft
        val right = currentRight
        curveJob = viewModelScope.launch(Dispatchers.Default) {
            val margin = (right - left) * 0.2f
            val hidden = store.hiddenMethods.value
            val curves = FunctionType.entries.mapNotNull { type ->
                if (type in hidden) return@mapNotNull null
                val result = store.results.value[type]
                if (result is FunctionResult.Success) {
                    type to buildCurves(
                        function = result.function,
                        left = left - margin,
                        right = right + margin
                    )
                } else null
            }.toMap()
            _graphState.update { it.copy(graph = curves) }
        }
    }

    private fun buildCurves(
        function: Function,
        left: Float,
        right: Float
    ): List<List<PointData>> {
        val fast = function.acceptVisitor(FastCalculator)
        val step = (right - left) / curvePoints
        val segments = mutableListOf<List<PointData>>()
        var current = mutableListOf<PointData>()

        for (i in 0..curvePoints) {
            val x = left + i * step
            val point = try {
                val y = fast.calculate(x.toDouble()).toFloat()
                if (y.isNaN() || y.isInfinite() || kotlin.math.abs(y) > Y_CLIP) null
                else PointData(x, y)
            } catch (_: Exception) {
                null
            }
            if (point != null) {
                current.add(point)
            } else if (current.isNotEmpty()) {
                segments.add(current)
                current = mutableListOf()
            }
        }
        if (current.isNotEmpty()) segments.add(current)
        return segments
    }

    fun addPoint(x: Float, y: Float) {
        if (store.rawPoints.value.size < Coordinates.MAX_SIZE) {
            val raw = RawMapper.toRaw(DataMapper.mapFrom(PointData(x, y)))
            val newSize = store.addRawPoint(raw)
            _graphState.update {
                it.copy(canAdd = newSize < Coordinates.MAX_SIZE)
            }
            if (newSize == Coordinates.MAX_SIZE) {
                showMessage(
                    "Добавлено максимальное количество точек в ${Coordinates.MAX_SIZE} единиц",
                    MessageType.WARNING
                )
            }
        } else {
            showMessage(
                "Превышено допустимое количество точек в ${Coordinates.MAX_SIZE} единиц",
                MessageType.ERROR
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        curveJob?.cancel()
    }
}
