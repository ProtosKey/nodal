package proxima.app.view.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import proxima.app.theme.LocalAppDimens

@Composable
fun Button(icon: ImageVector, label: String, description: String? = null) {
    Icon(icon, contentDescription = description)
    Spacer(Modifier.width(LocalAppDimens.current.paddingSmall))
    Text(text = label)
}
