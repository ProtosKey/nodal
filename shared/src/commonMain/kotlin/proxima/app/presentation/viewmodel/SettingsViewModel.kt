package proxima.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import proxima.app.data.MainStore
import proxima.app.presentation.state.SettingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SettingsViewModel(private val store: MainStore) : ViewModel() {
    private val _state = MutableStateFlow(SettingState())
    val state = _state.asStateFlow()

    init {
        store.settings.onEach { currentSettings ->
            _state.update { it.copy(settings = currentSettings) }
        }.launchIn(viewModelScope)
    }

    fun updateMathPrecision(key: String) = store.updateMathPrecision(key)
    fun updateGraphResolution(key: String) = store.updateGraphResolution(key)
    fun updateDisplayPrecision(key: String) = store.updateDisplayPrecision(key)
    fun updateStepX(key: String) = store.updateStepX(key)
}
