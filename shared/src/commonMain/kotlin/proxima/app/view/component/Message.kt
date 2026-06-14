package proxima.app.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import proxima.app.data.model.MessageType
import proxima.app.data.utils.Defaults
import proxima.app.theme.LocalAppDimens

@Composable
fun BoxScope.Message(
    message: String? = null,
    isVisible: Boolean = false,
    onClick: () -> Unit,
    bottom: Dp = 0.dp,
    type: MessageType = MessageType.ERROR
) {
    val colorScheme = MaterialTheme.colorScheme
    val containerColor = remember(type, colorScheme) {
        when (type) {
            MessageType.ERROR -> colorScheme.errorContainer
            MessageType.WARNING -> colorScheme.tertiaryContainer
            MessageType.GOOD -> colorScheme.secondaryContainer
        }
    }

    val textColor = remember(type, colorScheme) {
        when (type) {
            MessageType.ERROR -> colorScheme.onErrorContainer
            MessageType.WARNING -> colorScheme.onTertiaryContainer
            MessageType.GOOD -> colorScheme.onSecondaryContainer
        }
    }

    val icon = remember(type) {
        when (type) {
            MessageType.ERROR -> Icons.Default.ErrorOutline
            MessageType.WARNING -> Icons.Default.Info
            MessageType.GOOD -> Icons.Default.CheckCircleOutline
        }
    }

    AnimatedVisibility(
        visible = isVisible && message != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(
                bottom = LocalAppDimens.current.paddingMedium
            )
            .padding(
                bottom = if (bottom != 0.dp)
                    bottom + LocalAppDimens.current.paddingSmall
                else
                    bottom
            )
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() },
                shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium),
                color = containerColor,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(LocalAppDimens.current.paddingSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor
                    )
                    Text(
                        text = message ?: Defaults.exception(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
