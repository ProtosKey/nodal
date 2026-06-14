package proxima.app

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import org.koin.compose.KoinContext
import proxima.app.theme.AppTheme
import proxima.app.view.feature.graph.GraphScreen

@Preview
@Composable
fun App(
    onThemeChanged: @Composable (isDark: Boolean) -> Unit = {}
) = AppTheme(onThemeChanged) {
    KoinContext {
        Navigator(GraphScreen())
    }
}
