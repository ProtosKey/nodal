package proxima.app.theme

import androidx.compose.ui.graphics.Color
import proxima.app.data.model.FunctionType

private val methodPalette: Map<FunctionType, Color> = mapOf(
    FunctionType.LAGRANGE to Color(0xFFE53935),
    FunctionType.NEWTON_FORWARD to Color(0xFF1E88E5),
    FunctionType.NEWTON_BACKWARD to Color(0xFF8E24AA),
    FunctionType.GAUSS_FORWARD to Color(0xFF43A047),
    FunctionType.GAUSS_BACKWARD to Color(0xFFFB8C00),
    FunctionType.STIRLING to Color(0xFF00ACC1),
    FunctionType.BESSEL to Color(0xFF6D4C41),
)

fun methodColor(type: FunctionType): Color = methodPalette[type] ?: Color.Gray
