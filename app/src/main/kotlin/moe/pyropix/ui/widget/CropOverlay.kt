package moe.pyropix.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

@Composable
fun CropOverlay(
    cropRect: Rect,
    onCropChange: (Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragCorner by remember { mutableIntStateOf(-1) }
    val handleRadius = 24f

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { pos ->
                    val corners = listOf(
                        Offset(cropRect.left, cropRect.top),
                        Offset(cropRect.right, cropRect.top),
                        Offset(cropRect.right, cropRect.bottom),
                        Offset(cropRect.left, cropRect.bottom)
                    )
                    dragCorner = corners.indexOfFirst {
                        abs(pos.x - it.x) < handleRadius * 2 && abs(pos.y - it.y) < handleRadius * 2
                    }
                    if (dragCorner == -1 && cropRect.contains(pos)) dragCorner = 4
                },
                onDrag = { change, delta ->
                    change.consume()
                    val dx = delta.x; val dy = delta.y
                    val r = cropRect
                    val newRect = when (dragCorner) {
                        0 -> Rect(r.left + dx, r.top + dy, r.right, r.bottom)
                        1 -> Rect(r.left, r.top + dy, r.right + dx, r.bottom)
                        2 -> Rect(r.left, r.top, r.right + dx, r.bottom + dy)
                        3 -> Rect(r.left + dx, r.top, r.right, r.bottom + dy)
                        4 -> r.translate(dx, dy)
                        else -> r
                    }
                    if (newRect.width > 100 && newRect.height > 100) onCropChange(newRect)
                },
                onDragEnd = { dragCorner = -1 }
            )
        }
    ) {
        // Dim outside crop area
        val fullPath = Path().apply {
            addRect(Rect(0f, 0f, size.width, size.height))
        }
        val cropPath = Path().apply {
            addRect(cropRect)
        }
        val dimPath = Path.combine(androidx.compose.ui.graphics.PathOperation.Difference, fullPath, cropPath)
        drawPath(dimPath, Color.Black.copy(alpha = 0.6f))

        // Border
        drawRect(
            Color.White,
            topLeft = Offset(cropRect.left, cropRect.top),
            size = Size(cropRect.width, cropRect.height),
            style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f)))
        )

        // Corner handles
        val corners = listOf(
            Offset(cropRect.left, cropRect.top),
            Offset(cropRect.right, cropRect.top),
            Offset(cropRect.right, cropRect.bottom),
            Offset(cropRect.left, cropRect.bottom)
        )
        corners.forEach { c ->
            drawCircle(Color.White, radius = handleRadius, center = c)
            drawCircle(moe.pyropix.ui.theme.Brand, radius = handleRadius - 6f, center = c)
        }

        // Grid lines
        val third = cropRect.width / 3
        for (i in 1..2) {
            val x = cropRect.left + third * i
            drawLine(
                Color.White.copy(alpha = 0.5f),
                Offset(x, cropRect.top),
                Offset(x, cropRect.bottom),
                strokeWidth = 1f
            )
        }
        val thirdH = cropRect.height / 3
        for (i in 1..2) {
            val y = cropRect.top + thirdH * i
            drawLine(
                Color.White.copy(alpha = 0.5f),
                Offset(cropRect.left, y),
                Offset(cropRect.right, y),
                strokeWidth = 1f
            )
        }
    }
}
