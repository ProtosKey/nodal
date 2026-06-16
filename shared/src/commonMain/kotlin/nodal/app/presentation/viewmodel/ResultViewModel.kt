package nodal.app.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import nodal.app.data.MainStore
import nodal.app.data.model.FunctionResult
import nodal.app.data.model.FunctionType
import nodal.app.data.model.MessageType
import nodal.app.data.utils.Defaults
import nodal.app.domain.exception.EngineException
import nodal.app.domain.exception.InitException
import nodal.app.domain.math.DifferenceEngine
import nodal.app.domain.model.Coordinates
import nodal.app.presentation.basic.BaseViewModel
import nodal.app.presentation.mapper.RawMapper
import nodal.app.presentation.state.ResultState
import nodal.app.presentation.tools.StringParser
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultViewModel(store: MainStore) : BaseViewModel(store) {
    private val _resultState = MutableStateFlow(ResultState())
    val resultState = _resultState.asStateFlow()
    val notification = store.notification

    init {
        store.settings.onEach { settings ->
            _resultState.update {
                it.copy(
                    count = settings.mathPrecision.value.toLong(),
                    displayPrecision = settings.displayPrecision.value.toInt()
                )
            }
        }.launchIn(viewModelScope)

        combine(
            store.results,
            store.isLoading,
            store.diffTable,
            store.xNodes
        ) { results, isLoading, diffTable, xNodes ->
            _resultState.update {
                it.copy(
                    results = results,
                    isLoading = isLoading,
                    diffTable = diffTable,
                    xNodes = xNodes
                )
            }
        }.launchIn(viewModelScope)

        store.interpolateAt.onEach { value ->
            _resultState.update { it.copy(interpolateAt = value) }
        }.launchIn(viewModelScope)

        store.hiddenMethods.onEach { hidden ->
            _resultState.update { it.copy(hiddenMethods = hidden) }
        }.launchIn(viewModelScope)
    }

    fun updateInterpolateAt(value: String) {
        store.updateInterpolateAt(value)
    }

    fun toggleVisibility(type: FunctionType) {
        store.toggleMethodVisibility(type)
    }

    fun calculateResult() {
        if (_resultState.value.isLoading) return
        if (store.rawPoints.value.isEmpty()) {
            showMessage("Добавьте точки во вкладке «Ввод»", MessageType.ERROR)
            return
        }
        if (store.rawPoints.value.any { runCatching { RawMapper.toPoint(it) }.isFailure }) {
            showMessage("Исправьте некорректные точки перед расчётом", MessageType.ERROR)
            return
        }
        val xStr = store.interpolateAt.value.trim()
        if (xStr.isEmpty()) {
            showMessage("Введите аргумент x в поле выше", MessageType.ERROR)
            return
        }
        val xTarget = runCatching { StringParser.parseBigDecimal(xStr) }.getOrNull()
        if (xTarget == null) {
            showMessage("Некорректное значение x", MessageType.ERROR)
            return
        }
        _resultState.update { it.copy(interpolateAt = xStr) }

        viewModelScope.launch(Dispatchers.Default) {
            store.startLoading()
            try {
                val points = Coordinates(
                    store.rawPoints.value.map { RawMapper.toPoint(it) }.toMutableList()
                )
                val xList = points.map { it.x }
                val yList = points.map { it.y }
                val diffTable = runCatching {
                    DifferenceEngine.computeTable(yList)
                }.getOrElse { emptyList() }
                store.updateDiffTable(diffTable, xList)

                val count = _resultState.value.count
                val newResults = coroutineScope {
                    Defaults.solvers().map { (type, solver) ->
                        async {
                            type to try {
                                val function = solver.solve(points, count, StringParser.parseBigDecimal(_resultState.value.interpolateAt))
                                val value = function.calculate(xTarget, count)
                                val warning = function.warningAt(xTarget, count)
                                FunctionResult.Success(function, value, warning)
                            } catch (e: Exception) {
                                FunctionResult.Error(getMessageByError(e))
                            }
                        }
                    }.awaitAll().toMap()
                }
                store.updateFunctions(newResults)
            } catch (e: Exception) {
                showMessage(getMessageByError(e), MessageType.ERROR)
            } finally {
                store.endLoading()
            }
        }
    }

    private fun getMessageByError(e: Exception): String = when (e) {
        is EngineException, is InitException -> e.message
        else -> Defaults.exception()
    } ?: Defaults.exception()
}
