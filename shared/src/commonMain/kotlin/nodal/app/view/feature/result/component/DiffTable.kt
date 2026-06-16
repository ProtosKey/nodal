package nodal.app.view.feature.result.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Constraints
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import nodal.app.presentation.tools.StringParser
import nodal.app.theme.LocalAppDimens


@Composable
fun SummaryHint() {
    val dimens = LocalAppDimens.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusMedium),
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = dimens.paddingMedium,
                vertical = dimens.paddingSmall
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.paddingSmall)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Каждая формула оптимальна в своей области таблицы — подсказка «Неоптимальные данные» сигналит, когда x вне рекомендуемого диапазона.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun DiffTableView(
    xNodes: List<BigDecimal>,
    diffTable: List<List<BigDecimal>>,
    precision: Int
) {
    val dimens = LocalAppDimens.current
    val scroll = rememberScrollState()
    val n = xNodes.size

    val mono = FontFamily.Monospace

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(dimens.strokeThin, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(dimens.radiusMedium),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val targetWidthPx = constraints.maxWidth
            StretchingRow(
                modifier = Modifier.horizontalScroll(scroll),
                targetWidth = targetWidthPx
            ) {
                DiffColumn(header = "x", rowCount = n) { i ->
                    DiffCell(
                        text = StringParser.prepareToString(xNodes[i], precision),
                        color = MaterialTheme.colorScheme.onSurface,
                        mono = mono
                    )
                }
                for (k in diffTable.indices) {
                    DiffColumn(
                        header = if (k == 0) "y" else "Δ${superscript(k)}y",
                        rowCount = n
                    ) { i ->
                        if (i < diffTable[k].size) DiffCell(
                            text = StringParser.prepareToString(diffTable[k][i], precision),
                            color = MaterialTheme.colorScheme.onSurface,
                            mono = mono
                        ) else DiffCell(
                            text = "—",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            mono = mono
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StretchingRow(
    targetWidth: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val intrinsicWidths = measurables.map { it.maxIntrinsicWidth(Constraints.Infinity) }
        val totalIntrinsic = intrinsicWidths.sum()
        val count = measurables.size

        val shouldStretch =
            count > 0 && totalIntrinsic < targetWidth && targetWidth < Constraints.Infinity
        val widths = if (!shouldStretch) {
            intrinsicWidths
        } else {
            val extra = targetWidth - totalIntrinsic
            val perChild = extra / count
            val remainder = extra % count
            intrinsicWidths.mapIndexed { i, w -> w + perChild + if (i < remainder) 1 else 0 }
        }

        val placeables = widths.mapIndexed { i, w ->
            measurables[i].measure(
                Constraints(
                    minWidth = w,
                    maxWidth = w,
                    minHeight = 0,
                    maxHeight = constraints.maxHeight
                )
            )
        }

        val height = placeables.maxOfOrNull { it.height } ?: 0
        val totalWidth = widths.sum()

        layout(totalWidth, height) {
            var x = 0
            placeables.forEach {
                it.place(x, 0)
                x += it.width
            }
        }
    }
}

@Composable
private fun DiffColumn(
    header: String,
    rowCount: Int,
    cell: @Composable (Int) -> Unit
) {
    val dimens = LocalAppDimens.current
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = dimens.paddingSmall, vertical = dimens.paddingTiny),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = header,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        for (i in 0 until rowCount) {
            cell(i)
        }
    }
}

@Composable
private fun DiffCell(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    mono: FontFamily
) {
    val dimens = LocalAppDimens.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimens.paddingSmall,
                vertical = dimens.paddingMicro
            ),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = mono,
            color = color
        )
    }
}

private fun superscript(n: Int): String = when (n) {
    1 -> ""; 2 -> "²"; 3 -> "³"; 4 -> "⁴"; 5 -> "⁵"; 6 -> "⁶"; else -> "^$n"
}
