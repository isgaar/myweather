// Location: app/.../ui/components/art/VectorGlyphs.kt
// Function: Draws pure vector pin, water droplet, refresh arc, and menu icons
// via Compose Canvas paths replacing Unicode glyphs or emojis.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components.art

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun VectorDropletIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(10.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            quadraticTo(w * 0.95f, h * 0.6f, w * 0.5f, h)
            quadraticTo(w * 0.05f, h * 0.6f, w * 0.5f, 0f)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun VectorPinIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        drawCircle(color, w * 0.28f, Offset(w * 0.5f, h * 0.36f))
        val tip = Path().apply {
            moveTo(w * 0.26f, h * 0.44f)
            lineTo(w * 0.50f, h * 0.94f)
            lineTo(w * 0.74f, h * 0.44f)
            close()
        }
        drawPath(path = tip, color = color)
    }
}

@Composable
fun VectorRefreshIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        drawArc(
            color = color,
            startAngle = -35f,
            sweepAngle = 285f,
            useCenter = false,
            style = Stroke(width = 3.2f, cap = StrokeCap.Round)
        )
        val arrow = Path().apply {
            moveTo(w * 0.86f, h * 0.14f)
            lineTo(w * 0.94f, h * 0.36f)
            lineTo(w * 0.72f, h * 0.34f)
            close()
        }
        drawPath(path = arrow, color = color)
    }
}

@Composable
fun VectorMenuIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        listOf(0.26f, 0.50f, 0.74f).forEach { ry ->
            drawLine(
                color = color,
                start = Offset(w * 0.14f, h * ry),
                end = Offset(w * 0.86f, h * ry),
                strokeWidth = 3.2f,
                cap = StrokeCap.Round
            )
        }
    }
}
