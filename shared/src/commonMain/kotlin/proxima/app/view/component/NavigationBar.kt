package proxima.app.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import proxima.app.theme.LocalAppDimens
import proxima.app.view.feature.input.InputScreen
import proxima.app.view.feature.graph.GraphScreen
import proxima.app.view.feature.result.ResultsScreen
import proxima.app.view.feature.settings.SettingsScreen

@Composable
fun NavigationBar(navigator: Navigator) {
    val screen = navigator.lastItem

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .height(LocalAppDimens.current.navbar)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalAppDimens.current.strokeThin)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationElem(
                selected = screen is GraphScreen,
                onClick = { if (screen !is GraphScreen) navigator.replaceAll(GraphScreen()) },
                icon = Icons.Default.Home,
                label = "Главная"
            )

            NavigationElem(
                selected = screen is InputScreen,
                onClick = { if (screen !is InputScreen) navigator.replaceAll(InputScreen()) },
                icon = Icons.Default.Edit,
                label = "Ввод"
            )

            NavigationElem(
                selected = screen is ResultsScreen,
                onClick = { if (screen !is ResultsScreen) navigator.replaceAll(ResultsScreen()) },
                icon = Icons.Default.BarChart,
                label = "Результаты"
            )

            NavigationElem(
                selected = screen is SettingsScreen,
                onClick = { if (screen !is SettingsScreen) navigator.replaceAll(SettingsScreen()) },
                icon = Icons.Default.Settings,
                label = "Настройки"
            )
        }
    }
}
