package proxima.app.presentation.basic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import proxima.app.data.MainStore
import proxima.app.data.model.MessageType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseViewModel(protected val store: MainStore) : ViewModel(), HaveMessage {
    private var messageJob: Job? = null

    override fun hideMessage() {
        messageJob?.cancel()
        store.hideMessage()
    }

    override fun showMessage(message: String, messageType: MessageType) {
        messageJob?.cancel()
        messageJob = viewModelScope.launch {
            if (store.notification.value.isVisible) {
                store.hideMessage()
                delay(250)
            }
            store.showMessage(message, messageType)
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageJob?.cancel()
    }
}
