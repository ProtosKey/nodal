package proxima.app.view.feature.result.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.hrm.latex.renderer.Latex
import com.hrm.latex.renderer.model.LatexConfig
import proxima.app.theme.LocalAppDimens

@Composable
fun MetricResult(
    value: String,
    modifier: Modifier,
    label: String? = null,
    copy: String? = null,
) {
    val dimens = LocalAppDimens.current
    val scroll = rememberScrollState()
    val clipboard = LocalClipboardManager.current
    val surfaceColor = MaterialTheme.colorScheme.surface

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(
            color = MaterialTheme.colorScheme.outlineVariant,
            width = dimens.strokeThin,
        ),
        shape = RoundedCornerShape(dimens.radiusMedium),
        colors = CardDefaults.outlinedCardColors(containerColor = surfaceColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .drawWithContent {
                        drawContent()
                        if (copy != null) {
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    0.85f to Color.Transparent,
                                    1f to surfaceColor
                                )
                            )
                        }
                    }
                    .horizontalScroll(scroll)
                    .padding(
                        start = dimens.paddingMedium,
                        top = dimens.paddingMedium,
                        bottom = dimens.paddingMedium,
                        end = if (copy != null) dimens.paddingLarge else dimens.paddingMedium
                    ),
                verticalArrangement = Arrangement.spacedBy(dimens.paddingTiny)
            ) {
                if (label != null) {
                    Latex(
                        latex = label,
                        config = LatexConfig(
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Latex(
                    latex = value,
                    config = LatexConfig(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )
                )
            }

            if (copy != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { clipboard.setText(AnnotatedString(copy)) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Скопировать значение",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(dimens.iconMedium)
                    )
                }
            }
        }
    }
}
