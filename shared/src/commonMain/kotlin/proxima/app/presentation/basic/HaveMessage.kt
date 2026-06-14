package proxima.app.presentation.basic

import proxima.app.data.model.MessageType

interface HaveMessage {
    fun hideMessage()
    fun showMessage(message: String, messageType: MessageType)
}
