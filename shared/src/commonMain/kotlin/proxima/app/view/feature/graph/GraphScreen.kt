package proxima.app.view.feature.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import proxima.app.data.MainStore
import proxima.app.presentation.viewmodel.GraphViewModel
import proxima.app.theme.LocalAppDimens
import proxima.app.view.basic.factory
import proxima.app.view.component.Message
import proxima.app.view.component.NavigationBar
import proxima.app.view.component.Title
import proxima.app.view.feature.graph.component.Graph

class GraphScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val store = koinInject<MainStore>()
        val viewModel = viewModel<GraphViewModel>(factory = factory { GraphViewModel(store) })
        val state by viewModel.graphState.collectAsStateWithLifecycle()
        val message by viewModel.notification.collectAsStateWithLifecycle()
        val dimens = LocalAppDimens.current

        Scaffold(
            bottomBar = { NavigationBar(navigator) }
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
                    Title(label = "Главная")

                    if (state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimens.paddingExtraLarge),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Graph(
                                state = state,
                                onClick = viewModel::addPoint,
                                onRange = viewModel::updateVisible
                            )
                        }
                    }
                }

                Message(
                    message = message.message,
                    isVisible = message.isVisible,
                    onClick = { viewModel.hideMessage() },
                    bottom = 0.dp,
                    type = message.messageType
                )
            }
        }
    }
}
