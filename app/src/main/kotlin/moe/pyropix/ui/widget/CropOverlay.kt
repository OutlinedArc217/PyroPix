package moe.pyropix.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun CropOverlay(
    cropRect: Rect,
    onCropChange: (Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragCorner by remember { mutableIntStateOf(-1) }
    val handleRadius = 20f

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
                        (pos - it).getDistance() < handleRadius * 2
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
                    if (newRect.width > 50 && newRect.height > 50) onCropChange(newRect)
                },
                onDragEnd = { dragCorner = -1 }
            )
        }
    ) {
        // dim outside
        drawRect(Color.Black.copy(alpha = 0.5f))
        drawRect(Color.Transparent, topLeft = Offset(cropRect.left, cropRect.top), size = cropRect.size, blendMode = androidx.compose.ui.graphics.BlendMode.Clear)

        // border
        drawRect(
            Color.White,
            topLeft = Offset(cropRect.left, cropRect.top),
            size = cropRect.size,
            style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
        )

        // corner handles
        val corners = listOf(
            Offset(cropRect.left, cropRect.top),
            Offset(cropRect.right, cropRect.top),
            Offset(cropRect.right, cropRect.bottom),
            Offset(cropRect.left, cropRect.bottom)
        )
        corners.forEach { c ->
            drawCircle(Color.White, radius = handleRadius, center = c)
            drawCircle(moe.pyropix.ui.theme.Brand, radius = handleRadius - 4f, center = c)
        }
    }
}
