package nodal.app.view.feature.result.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import nodal.app.data.model.FunctionResult
import nodal.app.presentation.state.ResultState
import nodal.app.presentation.tools.StringParser
import nodal.app.theme.LocalAppDimens

@Composable
fun ResultLabel(
    label: String,
    color: Color,
    isHidden: Boolean,
    onToggleVisibility: () -> Unit,
    results: FunctionResult,
    state: ResultState
) {
    val dimens = LocalAppDimens.current
    when (results) {
        is FunctionResult.Success -> {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(dimens.strokeThin, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(dimens.radiusMedium),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = dimens.paddingMedium,
                        end = dimens.paddingMedium,
                        bottom = dimens.paddingMedium,
                        top = dimens.paddingSmall,
                    ),
                    verticalArrangement = Arrangement.spacedBy(dimens.paddingSmall)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FunctionLabel(
                            modifier = Modifier.weight(1f),
                            label = label,
                            color = MaterialTheme.colorScheme.onSurface,
                            dotColor = if (isHidden) color.copy(alpha = 0.3f) else color,
                        )
                        IconButton(
                            onClick = onToggleVisibility,
                            modifier = Modifier.size(dimens.iconLarge)
                        ) {
                            Icon(
                                imageVector = if (isHidden) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = if (isHidden) "Показать график"
                                else "Скрыть график",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(dimens.iconMedium)
                            )
                        }
                    }

                    if (results.warning != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(dimens.radiusMedium),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                        ) {
                            Row(
                                modifier = Modifier.padding(dimens.paddingSmall),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(dimens.paddingSmall)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(dimens.iconSmall)
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(dimens.paddingMicro)
                                ) {
                                    Text(
                                        text = "Неоптимальные данные",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = results.warning,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }

                    val params = results.function.acceptVisitor(state.params)
                    if (params != null) {
                        Text(
                            text = params,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val rawValue = StringParser.prepareToString(results.value)
                    val xText = state.interpolateAt
                    val copyText = if (xText.isNotEmpty())
                        "f($xText) = $rawValue"
                    else
                        "f(x) = $rawValue"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimens.paddingSmall)
                    ) {
                        MetricResult(
                            value = "\\approx \\textrm{$rawValue}",
                            copy = copyText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        is FunctionResult.Error -> {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(dimens.strokeThin, MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(dimens.radiusMedium),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = dimens.paddingMedium,
                        vertical = dimens.paddingSmall
                    ),
                    verticalArrangement = Arrangement.spacedBy(dimens.paddingTiny)
                ) {
                    FunctionLabel(
                        modifier = Modifier.fillMaxWidth(),
                        label = label,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(text = results.message, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
