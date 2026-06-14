package proxima.app.data.model

import proxima.app.data.utils.Defaults

data class Notification(
    val message: String = Defaults.message(),
    val messageType: MessageType = Defaults.messageType(),
    val isVisible: Boolean = Defaults.visible()
)
