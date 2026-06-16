package nodal.app.view.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import nodal.app.data.MainStore
import nodal.app.presentation.viewmodel.SettingsViewModel
import nodal.app.theme.LocalAppDimens
import nodal.app.view.basic.factory
import nodal.app.view.component.NavigationBar
import nodal.app.view.component.Title
import nodal.app.view.feature.settings.component.Parameter

class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val store = koinInject<MainStore>()
        val viewModel = viewModel<SettingsViewModel>(factory = factory { SettingsViewModel(store) })
        val state by viewModel.state.collectAsStateWithLifecycle()
        val settings = state.settings

        Scaffold(
            bottomBar = { NavigationBar(navigator) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = LocalAppDimens.current.paddingMedium)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingLarge)
            ) {
                Title(label = "Настройки")

                Column(verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingMedium)) {
                    Parameter(
                        setting = settings.mathPrecision,
                        onSelect = { viewModel.updateMathPrecision(it) }
                    )

                    Parameter(
                        setting = settings.graphResolution,
                        onSelect = { viewModel.updateGraphResolution(it) }
                    )

                    Parameter(
                        setting = settings.displayPrecision,
                        onSelect = { viewModel.updateDisplayPrecision(it) }
                    )

                    Parameter(
                        setting = settings.stepX,
                        onSelect = { viewModel.updateStepX(it) }
                    )
                }
            }
        }
    }
}