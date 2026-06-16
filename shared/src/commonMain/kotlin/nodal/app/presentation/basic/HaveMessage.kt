package nodal.app.presentation.basic

import nodal.app.data.model.MessageType

interface HaveMessage {
    fun hideMessage()
    fun showMessage(message: String, messageType: MessageType)
}
