package moe.pyropix.ui.solver

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import moe.pyropix.ui.theme.*
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(navCtrl: NavController, expr: String, vm: SolverVM = hiltViewModel()) {
    val points by vm.graphPoints.collectAsState()
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(expr) {
        if (points.isEmpty()) vm.plotGraph(expr)
    }

    val transformState = rememberTransformableState { zoom, pan, _ ->
        scale = (scale * zoom).coerceIn(0.2f, 5f)
        offsetX += pan.x
        offsetY += pan.y
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("函数图像") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "返回", tint = Brand)
                    }
                }
            )
        }
    ) { pad ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .transformable(transformState)
        ) {
            val w = size.width
            val h = size.height
            val cx = w / 2 + offsetX
            val cy = h / 2 + offsetY

            drawAxes(cx, cy, w, h, scale)

            if (points.size >= 2) {
                drawCurve(points, cx, cy, scale, w)
                drawSpecialPoints(points, cx, cy, scale, w)
            }
        }
    }
}

private fun DrawScope.drawAxes(cx: Float, cy: Float, w: Float, h: Float, scale: Float) {
    val axisColor = Color.Gray.copy(alpha = 0.6f)
    drawLine(axisColor, Offset(0f, cy), Offset(w, cy), strokeWidth = 1.5f)
    drawLine(axisColor, Offset(cx, 0f), Offset(cx, h), strokeWidth = 1.5f)

    val gridColor = Color.Gray.copy(alpha = 0.15f)
    val step = 50f * scale
    var x = cx % step
    while (x < w) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, h))
        x += step
    }
    var y = cy % step
    while (y < h) {
        drawLine(gridColor, Offset(0f, y), Offset(w, y))
        y += step
    }

    // tick labels
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.GRAY
        textSize = 24f
    }
    val unitPx = (size.width / 20f) * scale
    for (i in -10..10) {
        if (i == 0) continue
        val tx = cx + i * unitPx
        if (tx in 0f..w) {
            drawContext.canvas.nativeCanvas.drawText("$i", tx - 6, cy + 18, paint)
        }
        val ty = cy - i * unitPx
        if (ty in 0f..h) {
            drawContext.canvas.nativeCanvas.drawText("$i", cx + 4, ty + 6, paint)
        }
    }
}

private fun DrawScope.drawCurve(
    pts: List<Pair<Double, Double>>,
    cx: Float, cy: Float,
    scale: Float, w: Float
) {
    val unitPx = (w / 20f) * scale
    val path = Path()
    var started = false
    for ((px, py) in pts) {
        val sx = cx + (px * unitPx).toFloat()
        val sy = cy - (py * unitPx).toFloat()
        if (!started) { path.moveTo(sx, sy); started = true }
        else path.lineTo(sx, sy)
    }
    drawPath(path, GraphBlue, style = Stroke(width = 3f))
}

private fun DrawScope.drawSpecialPoints(
    pts: List<Pair<Double, Double>>,
    cx: Float, cy: Float,
    scale: Float, w: Float
) {
    val unitPx = (w / 20f) * scale
    for (i in 1 until pts.size - 1) {
        val (px, py) = pts[i]
        val sx = cx + (px * unitPx).toFloat()
        val sy = cy - (py * unitPx).toFloat()

        // zeros
        if (abs(py) < 0.05) {
            drawCircle(LatexGreen, radius = 6f, center = Offset(sx, sy))
        }

        // local extrema
        val prev = pts[i - 1].second
        val next = pts[i + 1].second
        if ((py > prev && py > next) || (py < prev && py < next)) {
            drawCircle(SolveOrange, radius = 6f, center = Offset(sx, sy))
        }
    }
}
