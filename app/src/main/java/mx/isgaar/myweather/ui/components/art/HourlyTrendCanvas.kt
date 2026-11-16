// Location: app/.../ui/components/art/HourlyTrendCanvas.kt
// Function: Draws the smooth cubic-Bezier hourly temperature trend line and
// circular data dots inside the horizontal HourlyCurveCard strip.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components.art

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.domain.model.HourlyPoint
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette

@Composable
fun HourlyTrendCanvas(
    items: List<HourlyPoint>,
    itemW: Dp,
    minT: Double,
    span: Double
) {
    val pal = LocalWeatherPalette.current
    val wBox = itemW * items.size.coerceAtLeast(1)
    Box(Modifier.width(wBox).height(34.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            if (items.size >= 2) {
                val step = size.width / items.size
                val pts = items.mapIndexed { i, pt ->
                    val n = ((pt.temperatureC - minT) / span).toFloat()
                    val y = size.height * (0.82f - n * 0.64f)
                    Offset(step * i + step * 0.5f, y)
                }
                val path = Path().apply {
                    moveTo(pts.first().x, pts.first().y)
                    for (i in 1 until pts.size) {
                        val p = pts[i - 1]
                        val c = pts[i]
                        val mx = (p.x + c.x) / 2f
                        cubicTo(mx, p.y, mx, c.y, c.x, c.y)
                    }
                }
                val st = Stroke(4f, cap = StrokeCap.Round)
                drawPath(path, pal.accentCurve, style = st)
                pts.forEach { drawCircle(pal.textPrimary, 5f, it) }
            }
        }
    }
}

