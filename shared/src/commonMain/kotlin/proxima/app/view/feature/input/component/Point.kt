package proxima.app.view.feature.input.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import proxima.app.presentation.model.PointEntry
import proxima.app.theme.LocalAppDimens

@Composable
fun Point(
    index: Int,
    point: PointEntry,
    onUpdate: (Int, String, String) -> Unit,
    onDelete: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            LocalAppDimens.current.paddingSmall
        ),
    ) {
        /*
        Text(
            modifier = Modifier.size(LocalAppDimens.current.paddingLarge),
            text = "${index + 1}.",
            style = MaterialTheme.typography.bodyLarge,
        )
         */
        val fieldColors = if (!point.isValid) OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.error,
            unfocusedBorderColor = MaterialTheme.colorScheme.error
        ) else OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = point.x,
            shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium),
            onValueChange = { onUpdate(index, it, point.y) },
            placeholder = { PlaceHolder("X", index) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = fieldColors
        )

        OutlinedTextField(
            value = point.y,
            shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium),
            onValueChange = { onUpdate(index, point.x, it) },
            placeholder = { PlaceHolder("Y", index) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = fieldColors
        )

        FilledIconButton(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium),
            onClick = { onDelete(index) },
            enabled = true,
            colors =
                IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.onError
            )
        }
    }
}
