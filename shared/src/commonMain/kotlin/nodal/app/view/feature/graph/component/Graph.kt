package nodal.app.view.feature.graph.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nodal.app.presentation.state.GraphState
import nodal.app.theme.LocalAppDimens
import nodal.app.theme.methodColor
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun Graph(
    state: GraphState,
    onClick: (x: Float, y: Float) -> Unit,
    onRange: (minX: Float, maxX: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val density = LocalDensity.current
    val dimens = LocalAppDimens.current
    val curveStrokeWidth = with(density) { dimens.strokeThick.toPx() }

    var minHor by remember { mutableStateOf(-10f) }
    var maxHor by remember { mutableStateOf(10f) }
    var minVer by remember { mutableStateOf(-10f) }
    var maxVer by remember { mutableStateOf(10f) }

    var zoom by remember { mutableStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(Offset(1f, 1f)) }

    val padding = 80f

    fun toMath(offset: Offset): Offset {
        val dw = canvasSize.x - padding * 2
        val dh = canvasSize.y - padding * 2
        val mx = ((offset.x - panOffset.x) / zoom - padding) / dw * (maxHor - minHor) + minHor
        val my = maxVer - (((offset.y - panOffset.y) / zoom - padding) / dh * (maxVer - minVer))
        return Offset(mx, my)
    }

    LaunchedEffect(zoom, panOffset, canvasSize, minHor, maxHor) {
        delay(50)
        val topLeft = toMath(Offset.Zero)
        val bottomRight = toMath(canvasSize)
        onRange(
            min(topLeft.x, bottomRight.x),
            max(topLeft.x, bottomRight.x),
        )
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = colors.onSurfaceVariant.copy(alpha = 0.5f),
        fontSize = MaterialTheme.typography.bodySmall.fontSize
    )
    val axisLabelStyle = TextStyle(
        color = colors.onSurfaceVariant, fontSize = MaterialTheme.typography.bodyLarge.fontSize
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .onGloballyPositioned {
                canvasSize = Offset(it.size.width.toFloat(), it.size.height.toFloat())
            }
            .pointerInput(zoom, panOffset, canvasSize, minHor, maxHor) {
                detectTapGestures { tapOffset ->
                    val pos = toMath(tapOffset)
                    onClick(pos.x, pos.y)
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoomChange, _ ->
                    val newZoom = (zoom * zoomChange).coerceIn(0.1f, 30f)
                    panOffset = centroid + (panOffset - centroid) * (newZoom / zoom) + pan
                    zoom = newZoom
                }
            }
    ) {
        val drawWidth = size.width - padding * 2
        val drawHeight = size.height - padding * 2

        fun toOffset(mathX: Float, mathY: Float): Offset {
            val xProgress = (mathX - minHor) / (maxHor - minHor)
            val yProgress = (maxVer - mathY) / (maxVer - minVer)
            val base = Offset(padding + xProgress * drawWidth, padding + yProgress * drawHeight)
            return base * zoom + panOffset
        }

        val topLeftM = toMath(Offset.Zero)
        val bottomRightM = toMath(Offset(size.width, size.height))

        val zeroPos = toOffset(0f, 0f)

        drawGrid(
            minHorizon = min(topLeftM.x, bottomRightM.x),
            maxHorizon = max(topLeftM.x, bottomRightM.x),
            minVertical = min(topLeftM.y, bottomRightM.y),
            maxVertical = max(topLeftM.y, bottomRightM.y),
            color = colors.outlineVariant.copy(alpha = .4f),
            measurer = textMeasurer,
            style = labelStyle,
            offset = ::toOffset
        )

        drawLine(
            colors.outlineVariant,
            Offset(zeroPos.x, 0f),
            Offset(zeroPos.x, size.height),
            1.dp.toPx()
        )
        drawLine(
            colors.outlineVariant, Offset(0f, zeroPos.y), Offset(size.width, zeroPos.y), 1.dp.toPx()
        )

        drawText(textMeasurer, "Y", Offset(zeroPos.x + 10f, 10f), axisLabelStyle)
        drawText(textMeasurer, "X", Offset(size.width - 40f, zeroPos.y - 55f), axisLabelStyle)

        state.graph.forEach { (type, segments) ->
            val color = methodColor(type)
            segments.forEach { points ->
                val path = Path()
                var first = true
                points.forEach { p ->
                    val pos = toOffset(p.x, p.y)
                    if (first) {
                        path.moveTo(pos.x, pos.y)
                        first = false
                    } else {
                        path.lineTo(pos.x, pos.y)
                    }
                }
                drawPath(path = path, color = color, style = Stroke(width = curveStrokeWidth))
            }
        }

        state.points.forEach { point ->
            val center = toOffset(point.x, point.y)
            drawCircle(color = colors.primary, radius = 18f, center = center)
            drawCircle(color = colors.background, radius = 12f, center = center)
        }

        state.interpolationPoint?.let { p ->
            val center = toOffset(p.x, p.y)
            drawCircle(color = colors.tertiary, radius = 22f, center = center)
            drawCircle(color = colors.background, radius = 14f, center = center)
            drawCircle(color = colors.tertiary, radius = 6f, center = center)
        }
    }
}

private fun DrawScope.drawGrid(
    minHorizon: Float,
    maxHorizon: Float,
    minVertical: Float,
    maxVertical: Float,
    color: Color,
    measurer: TextMeasurer,
    style: TextStyle,
    offset: (Float, Float) -> Offset,
    stroke: Dp = 1.dp
) {
    val horizon = getGridStep((maxHorizon - minHorizon) / 8)
    val vertical = getGridStep((maxVertical - minVertical) / 16)

    var current = floor(minHorizon / horizon) * horizon
    while (current <= maxHorizon + horizon) {
        val value = offset(current, 0f).x
        if (value in 0f..size.width) {
            drawLine(
                color = color,
                start = Offset(value, 0f),
                end = Offset(value, size.height),
                strokeWidth = stroke.toPx()
            )
            drawText(
                textMeasurer = measurer,
                text = format(current),
                style = style,
                topLeft = Offset(value + 15f, size.height - 50f)
            )
        }
        current += horizon
    }

    var currentVertical = floor(minVertical / vertical) * vertical
    while (currentVertical <= maxVertical + vertical) {
        val canvasY = offset(0f, currentVertical).y
        if (canvasY in 0f..size.height) {
            drawLine(
                color = color,
                start = Offset(0f, canvasY),
                end = Offset(size.width, canvasY),
                strokeWidth = stroke.toPx()
            )
            drawText(
                textMeasurer = measurer,
                text = format(currentVertical),
                style = style,
                topLeft = Offset(20f, canvasY + 10f)
            )
        }
        currentVertical += vertical
    }
}

private fun getGridStep(step: Float): Float {
    if (step <= 0f) return 1f
    val magnitude = 10.0.pow(floor(log10(step.toDouble()))).toFloat()
    val res = step / magnitude
    return magnitude * when {
        res < 1.5f -> 1f
        res < 3.5f -> 2f
        res < 7.5f -> 5f
        else -> 10f
    }
}

private fun format(value: Float): String {
    if (abs(value) < 1e-5) return "0"
    return if (value == value.toInt().toFloat()) value.toInt().toString()
    else (round(value * 100) / 100.0).toString()
}