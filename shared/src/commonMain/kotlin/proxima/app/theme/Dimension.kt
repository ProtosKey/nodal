package proxima.app.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalAppDimens = staticCompositionLocalOf { AppDimens }

object AppDimens {
    val paddingTiny = 4.dp
    val paddingSmall = 8.dp
    val paddingMedium = 16.dp
    val paddingLarge = 24.dp
    val paddingExtraLarge = 32.dp

    val fontCaption = 12.sp
    val fontBody = 16.sp
    val fontSubtitle = 20.sp
    val fontTitle = 26.sp
    val fontHeader = 32.sp

    val strokeThin = 1.dp
    val strokeThick = 2.dp

    val message = 48.dp
    val navbar = 84.dp

    val iconSmall = 20.dp
    val iconMedium = 24.dp
    val iconLarge = 48.dp

    val radiusSmall = 4.dp
    val radiusMedium = 12.dp
    val radiusLarge = 24.dp

    val chartHeight = 280.dp
    val chartPointSize = 6.dp
    val axisLabelPadding = 8.dp

    val paddingMicro = 2.dp

    val legendDot = 12.dp
}
