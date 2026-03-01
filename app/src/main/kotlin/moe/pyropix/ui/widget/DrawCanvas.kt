package moe.pyropix.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import android.graphics.Bitmap

data class DrawPath(val points: List<Offset>, val color: Color, val width: Float, val isEraser: Boolean = false) {
    val path: Path by lazy {
        Path().apply {
            if (points.size >= 2) {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
        }
    }
}

@Composable
fun DrawCanvas(
    paths: List<DrawPath>,
    currentPath: List<Offset>,
    penColor: Color,
    penWidth: Float,
    isEraser: Boolean,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { onDragStart(it) },
                onDrag = { change, _ ->
                    change.consume()
                    onDrag(change.position)
                },
                onDragEnd = { onDragEnd() }
            )
        }
    ) {
        for (p in paths) {
            if (p.points.size < 2) continue
            drawPath(
                p.path,
                color = if (p.isEraser) Color.White else p.color,
                style = Stroke(width = p.width, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        if (currentPath.size >= 2) {
            val path = Path().apply {
                moveTo(currentPath[0].x, currentPath[0].y)
                for (i in 1 until currentPath.size) {
                    lineTo(currentPath[i].x, currentPath[i].y)
                }
            }
            drawPath(
                path,
                color = if (isEraser) Color.White else penColor,
                style = Stroke(width = if (isEraser) penWidth * 3 else penWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

fun canvasToBitmap(width: Int, height: Int, paths: List<DrawPath>): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)
    canvas.drawColor(android.graphics.Color.WHITE)
    for (p in paths) {
        if (p.points.size < 2) continue
        val paint = android.graphics.Paint().apply {
            color = if (p.isEraser) android.graphics.Color.WHITE else p.color.toArgb()
            strokeWidth = p.width
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
            isAntiAlias = true
        }
        val path = android.graphics.Path().apply {
            moveTo(p.points[0].x, p.points[0].y)
            for (i in 1 until p.points.size) lineTo(p.points[i].x, p.points[i].y)
        }
        canvas.drawPath(path, paint)
    }
    return bmp
}
