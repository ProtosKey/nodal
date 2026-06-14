package proxima.app.view.feature.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import org.koin.compose.koinInject
import proxima.app.data.MainStore
import proxima.app.presentation.tools.StringParser
import proxima.app.presentation.viewmodel.ResultViewModel
import proxima.app.theme.LocalAppDimens
import proxima.app.theme.methodColor
import proxima.app.view.basic.factory
import proxima.app.view.component.Message
import proxima.app.view.component.NavigationBar
import proxima.app.view.component.TargetField
import proxima.app.view.component.Title
import proxima.app.view.component.Empty
import proxima.app.view.feature.result.component.DiffTableView
import proxima.app.view.feature.result.component.ResultLabel
import proxima.app.view.feature.result.component.SummaryHint

class ResultsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val store = koinInject<MainStore>()
        val viewModel = viewModel<ResultViewModel>(factory = factory { ResultViewModel(store) })
        val state by viewModel.resultState.collectAsStateWithLifecycle()
        val message by viewModel.notification.collectAsStateWithLifecycle()
        var height by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        val dimens = LocalAppDimens.current

        Scaffold(
            bottomBar = { NavigationBar(navigator) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    modifier = Modifier.onGloballyPositioned { add ->
                        height = with(density) { add.size.height.toDp() }
                    },
                    onClick = { viewModel.calculateResult() },
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
                    proxima.app.view.component.Button(
                        icon = Icons.Default.QueryStats,
                        label = "Посчитать",
                        description = "Посчитать результат"
                    )
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
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(dimens.paddingSmall)
                ) {
                    Title(label = "Результаты")

                    TargetField(
                        value = state.interpolateAt,
                        onValueChange = viewModel::updateInterpolateAt
                    )

                    if (state.diffTable.isNotEmpty() || state.results.entries.isNotEmpty() || state.isLoading) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(dimens.paddingSmall),
                            contentPadding = PaddingValues(
                                bottom = height + dimens.paddingLarge
                            ),
                        ) {
                            if (state.diffTable.isNotEmpty()) {
                                item(key = "diff_table") {
                                    DiffTableView(
                                        xNodes = state.xNodes,
                                        diffTable = state.diffTable,
                                        precision = state.displayPrecision
                                    )
                                }
                            }

                            if (!state.isLoading && state.results.isNotEmpty()) {
                                item(key = "summary") { SummaryHint() }
                            }

                            if (state.isLoading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(dimens.paddingExtraLarge),
                                        contentAlignment = Alignment.Center
                                    ) { CircularProgressIndicator() }
                                }
                            } else {
                                items(
                                    items = state.results.entries.toList(),
                                    key = { (type, _) -> type }
                                ) { (type, result) ->
                                    ResultLabel(
                                        label = type.label,
                                        color = methodColor(type),
                                        isHidden = type in state.hiddenMethods,
                                        onToggleVisibility = { viewModel.toggleVisibility(type) },
                                        results = result,
                                        state = state
                                    )
                                }
                            }
                        }
                    } else {
                        Empty(
                            help = "Введите точки и x во вкладке «Ввод», затем нажмите «Посчитать»",
                            icon = Icons.Default.QueryStats
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
