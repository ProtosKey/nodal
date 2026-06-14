package proxima.app.view.feature.input.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import proxima.app.presentation.tools.StringParser
import proxima.app.theme.LocalAppDimens

@Composable
fun Dialog(
    onDismiss: () -> Unit,
    onGenerate: (fn: String, from: String, to: String, count: String) -> Unit
) {
    val functions = listOf("sin", "cos", "exp", "ln")
    var selectedFn by remember { mutableStateOf("sin") }
    var from by remember { mutableStateOf("0") }
    var to by remember { mutableStateOf("pi") }
    var count by remember { mutableStateOf("7") }

    val computedH = remember(from, to, count) {
        val a = runCatching { StringParser.parseBigDecimal(from).doubleValue(false) }.getOrNull()
        val b = runCatching { StringParser.parseBigDecimal(to).doubleValue(false) }.getOrNull()
        val n = count.toIntOrNull()
        if (a != null && b != null && n != null && n > 1 && b > a)
            "Шаг модели = ${
                StringParser.prepareToString(
                    com.ionspin.kotlin.bignum.decimal.BigDecimal.fromDouble(
                        (b - a) / (n - 1)
                    ), 6
                )
            }"
        else "Шаг модели = ..."
    }

    val dimens = LocalAppDimens.current
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedBorderColor = MaterialTheme.colorScheme.primary
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimens.radiusLarge),
            color = MaterialTheme.colorScheme.surface
        ) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                Column(
                    modifier = Modifier.padding(dimens.paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(dimens.paddingSmall)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(dimens.paddingSmall)) {
                        functions.forEach { fn ->
                            FilterChip(
                                shape = RoundedCornerShape(dimens.radiusLarge),
                                selected = fn == selectedFn,
                                onClick = { selectedFn = fn },
                                label = { Text(fn) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    LabeledField(
                        label = "Начало интервала",
                        value = from,
                        onValueChange = { from = it },
                        fieldColors = fieldColors,
                    )

                    LabeledField(
                        label = "Конец интервала",
                        value = to,
                        onValueChange = { to = it },
                        fieldColors = fieldColors
                    )

                    LabeledField(
                        label = "Количество точек n",
                        value = count,
                        onValueChange = { count = it },
                        fieldColors = fieldColors
                    )

                    Text(
                        text = computedH,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Отмена") }

                        TextButton(onClick = { onGenerate(selectedFn, from, to, count) }) {
                            Text("Сгенерировать")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fieldColors: androidx.compose.material3.TextFieldColors
) {
    val dimens = LocalAppDimens.current
    Column(verticalArrangement = Arrangement.spacedBy(dimens.paddingTiny)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            shape = RoundedCornerShape(dimens.radiusMedium),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

