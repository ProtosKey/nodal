package nodal.app.view.feature.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import nodal.app.data.MainStore
import nodal.app.presentation.viewmodel.InputViewModel
import nodal.app.theme.LocalAppDimens
import nodal.app.view.basic.factory
import nodal.app.view.component.Button
import nodal.app.view.component.Message
import nodal.app.view.component.NavigationBar
import nodal.app.view.component.TargetField
import nodal.app.view.component.Title
import nodal.app.view.component.Empty
import nodal.app.view.feature.input.component.Dialog
import nodal.app.view.feature.input.component.Point

class InputScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val store = koinInject<MainStore>()
        val viewModel = viewModel<InputViewModel>(factory = factory { InputViewModel(store) })
        val state by viewModel.inputState.collectAsStateWithLifecycle()
        val message by viewModel.notification.collectAsStateWithLifecycle()
        var height by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        val dimens = LocalAppDimens.current
        var showGenerateDialog by remember { mutableStateOf(false) }

        if (showGenerateDialog) {
            Dialog(
                onDismiss = { showGenerateDialog = false },
                onGenerate = { fn, from, to, count ->
                    viewModel.generateFromFunction(fn, from, to, count)
                    showGenerateDialog = false
                }
            )
        }

        Scaffold(
            bottomBar = { NavigationBar(navigator) },
            floatingActionButton = {
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .onGloballyPositioned { add ->
                            height = with(density) { add.size.height.toDp() }
                        },
                    verticalArrangement = Arrangement.spacedBy(dimens.paddingSmall)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { showGenerateDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        shape = RoundedCornerShape(dimens.radiusMedium),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            focusedElevation = 0.dp,
                            hoveredElevation = 0.dp
                        )
                    ) {
                        Button(
                            icon = Icons.Default.AutoAwesome,
                            label = "Из функции",
                            description = "Сгенерировать точки из функции"
                        )
                    }

                    ExtendedFloatingActionButton(
                        onClick = { viewModel.addPoint() },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(dimens.radiusMedium),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            focusedElevation = 0.dp,
                            hoveredElevation = 0.dp
                        )
                    ) {
                        Button(
                            icon = Icons.Default.Add,
                            label = "Добавить",
                            description = "Добавить новую точку"
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = dimens.paddingMedium)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(dimens.paddingSmall)
                ) {
                    Title(label = "Ввод точек")

                    TargetField(
                        value = state.interpolateAt,
                        onValueChange = viewModel::updateInterpolateAt
                    )

                    if (state.input.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(dimens.paddingSmall),
                            contentPadding = PaddingValues(
                                bottom = height + dimens.paddingLarge
                            ),
                        ) {
                            itemsIndexed(
                                items = state.input,
                                key = { index, _ -> index }
                            ) { index, point ->
                                Point(
                                    index = index,
                                    point = point,
                                    onUpdate = { idx: Int, x: String, y: String ->
                                        viewModel.updatePoint(idx, x, y)
                                    },
                                    onDelete = { idx: Int -> viewModel.removeByIndex(idx) }
                                )
                            }
                        }
                    } else {
                        Empty(
                            help = "Нажмите «Добавить» или сгенерируйте точки из функции",
                            icon = Icons.Default.Add
                        )
                    }
                }

                Message(
                    message = message.message,
                    isVisible = message.isVisible,
                    onClick = { viewModel.hideMessage() },
                    bottom = height,
                    type = message.messageType
                )
            }
        }
    }
}

