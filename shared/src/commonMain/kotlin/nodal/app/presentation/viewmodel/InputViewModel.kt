package nodal.app.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import nodal.app.data.MainStore
import nodal.app.data.model.MessageType
import nodal.app.data.model.RawPoint
import nodal.app.data.utils.Defaults
import nodal.app.domain.model.Coordinates
import nodal.app.domain.model.Point
import nodal.app.domain.utils.PointFactory
import nodal.app.presentation.basic.BaseViewModel
import nodal.app.presentation.exception.ModelException
import nodal.app.presentation.mapper.RawMapper
import nodal.app.presentation.state.InputState
import nodal.app.presentation.tools.StringParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sin

class InputViewModel(store: MainStore) : BaseViewModel(store) {
    private val _inputState = MutableStateFlow(InputState(interpolateAt = store.interpolateAt.value))
    val inputState = _inputState.asStateFlow()
    val notification = store.notification

    fun updateInterpolateAt(value: String) {
        _inputState.update { it.copy(interpolateAt = value) }
        store.updateInterpolateAt(value)
    }

    init {
        store.rawPoints.onEach { rawPoints ->
            if (rawPoints.size != _inputState.value.input.size) {
                _inputState.update {
                    it.copy(
                        input = rawPoints.map(RawMapper::toEntry),
                        canAdd = rawPoints.size < Coordinates.MAX_SIZE
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updatePoint(index: Int, x: String, y: String) {
        try {
            checkIndex(index)
            val newInput = _inputState.value.input.toMutableList()
            newInput[index] = RawMapper.toEntry(RawPoint(x, y))
            _inputState.update { it.copy(input = newInput) }
            store.updateRawPoint(index, x, y)
        } catch (e: ModelException) {
            showMessage(e.message ?: Defaults.message(), MessageType.ERROR)
        }
    }

    fun addPoint() {
        if (store.rawPoints.value.size < Coordinates.MAX_SIZE) {
            val point = Point(nextX(), PointFactory.randomValue())
            val newSize = store.addRawPoint(RawMapper.toRaw(point))
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

    private fun nextX(): BigDecimal {
        val xs = store.rawPoints.value.mapNotNull { raw ->
            runCatching { StringParser.parseBigDecimal(raw.x) }.getOrNull()
        }
        if (xs.isEmpty()) return BigDecimal.ZERO

        val step = runCatching {
            StringParser.parseBigDecimal(store.settings.value.stepX.value)
        }.getOrElse { BigDecimal.ONE }
        return xs.max() + step
    }

    fun removeByIndex(index: Int) {
        try {
            checkIndex(index)
            store.removeRawPoint(index)
        } catch (e: ModelException) {
            showMessage(e.message ?: Defaults.message(), MessageType.ERROR)
        }
    }

    fun loadPreset(preset: List<Pair<String, String>>) {
        store.setRawPoints(preset.map { (x, y) -> RawPoint(x, y) })
    }

    fun generateFromFunction(functionName: String, from: String, to: String, count: String) {
        val a = runCatching { StringParser.parseBigDecimal(from).doubleValue(false) }.getOrNull()
        val b = runCatching { StringParser.parseBigDecimal(to).doubleValue(false) }.getOrNull()
        val n = count.toIntOrNull()

        if (a == null || b == null || n == null || n < 3 || a >= b) {
            showMessage("Проверьте параметры: n ≥ 3, начало < конец", MessageType.ERROR)
            return
        }
        if (n > Coordinates.MAX_SIZE) {
            showMessage("Количество точек не может превышать ${Coordinates.MAX_SIZE}", MessageType.ERROR)
            return
        }

        val fn: (Double) -> Double = when (functionName) {
            "sin" -> { x -> sin(x) }
            "cos" -> { x -> cos(x) }
            "exp" -> { x -> exp(x) }
            "ln"  -> { x -> if (x > 0) ln(x) else Double.NaN }
            else  -> { x -> sin(x) }
        }

        viewModelScope.launch(Dispatchers.Default) {
            val points = mutableListOf<Pair<String, String>>()
            for (i in 0 until n) {
                val x = a + i * (b - a) / (n - 1)
                val y = fn(x)
                if (y.isNaN() || y.isInfinite()) {
                    showMessage("Функция не определена на заданном интервале", MessageType.ERROR)
                    return@launch
                }
                points.add(
                    StringParser.prepareToString(BigDecimal.fromDouble(x)) to
                    StringParser.prepareToString(BigDecimal.fromDouble(y))
                )
            }
            loadPreset(points)
            showMessage("Сгенерировано $n точек ($functionName)", MessageType.GOOD)
        }
    }

    private fun checkIndex(index: Int) {
        if (index < 0 || index >= _inputState.value.input.size)
            throw ModelException("Точки с таким индексом не существует")
    }
}
