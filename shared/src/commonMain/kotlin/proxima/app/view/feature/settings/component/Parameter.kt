package proxima.app.view.feature.settings.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import proxima.app.data.model.Setting
import proxima.app.theme.LocalAppDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Parameter(
    setting: Setting,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingTiny)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = setting.label,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.onBackground
            )
            Text(
                text = setting.description,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onBackground
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = setting.current,
                onValueChange = {},
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = colors.onBackground
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colors.outlineVariant,
                    focusedBorderColor = colors.primary,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                )
            )

            MaterialTheme(
                shapes = MaterialTheme.shapes.copy(extraSmall = MaterialTheme.shapes.large)
            ) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = colors.surface,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    border = BorderStroke(
                        LocalAppDimens.current.strokeThin,
                        colors.outlineVariant
                    )
                ) {
                    setting.options.keys.forEach { optionKey ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = optionKey,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = colors.onSurfaceVariant
                                )
                            },
                            onClick = {
                                onSelect(optionKey)
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = colors.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}
