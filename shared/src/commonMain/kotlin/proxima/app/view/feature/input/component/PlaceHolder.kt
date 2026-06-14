package proxima.app.view.feature.input.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.hrm.latex.renderer.Latex
import com.hrm.latex.renderer.model.LatexConfig

@Composable
fun PlaceHolder(label: String, index: Int) {
    Latex(
        latex = "\\textsf{$label}_{${index + 1}}",
        config = LatexConfig(
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        ),
    )
}
