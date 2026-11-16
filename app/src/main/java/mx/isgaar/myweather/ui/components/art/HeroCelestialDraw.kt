// Location: app/.../ui/components/art/HeroCelestialDraw.kt
// Function: Canvas drawing routines for sun, crescent moon, clouds, and
// precipitation layers rendered inside WeatherHeroArt without bitmaps.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import mx.isgaar.myweather.domain.model.WeatherCondition

object HeroCelestialDraw {

    fun drawSun(
        s: DrawScope,
        c: Offset,
        r: Float,
        sun: Color,
        warm: Color
    ) {
        s.drawCircle(sun.copy(alpha = 0.22f), r * 1.42f, c)
        s.drawCircle(Brush.radialGradient(listOf(sun, warm), c, r), r, c)
    }

    fun drawCrescent(
        s: DrawScope,
        c: Offset,
        r: Float,
        moon: Color,
        warm: Color,
        sky: Color,
        star: Color
    ) {
        s.drawCircle(moon.copy(alpha = 0.14f), r * 1.38f, c)
        val p0 = Offset(c.x - r, c.y - r)
        val p1 = Offset(c.x + r, c.y + r)
        s.drawCircle(Brush.linearGradient(listOf(moon, warm), p0, p1), r, c)
        s.drawCircle(sky, r * 0.84f, Offset(c.x - r * 0.42f, c.y - r * 0.28f))
        s.drawCircle(
            warm.copy(alpha = 0.6f), r * 0.14f,
            Offset(c.x + r * 0.28f, c.y + r * 0.22f)
        )
        s.drawCircle(
            warm.copy(alpha = 0.6f), r * 0.09f,
            Offset(c.x + r * 0.55f, c.y - r * 0.08f)
        )
        listOf(
            Offset(c.x - r * 0.95f, c.y - r * 0.85f),
            Offset(c.x - r * 1.05f, c.y + r * 0.55f),
            Offset(c.x + r * 0.88f, c.y - r * 0.92f)
        ).forEach { pt -> s.drawCircle(star.copy(alpha = 0.7f), 2.6f, pt) }
    }

    fun drawCloud(s: DrawScope, w: Float, h: Float, color: Color) {
        s.drawCircle(color, w * 0.20f, Offset(w * 0.38f, h * 0.56f))
        s.drawCircle(color, w * 0.24f, Offset(w * 0.56f, h * 0.50f))
        s.drawCircle(color, w * 0.17f, Offset(w * 0.72f, h * 0.58f))
        s.drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.20f, h * 0.56f),
            size = Size(w * 0.66f, h * 0.20f)
        )
    }

    fun drawPrecip(
        s: DrawScope,
        cond: WeatherCondition,
        w: Float,
        h: Float,
        rain: Color,
        bolt: Color
    ) {
        val isRain = cond in listOf(
            WeatherCondition.DRIZZLE, WeatherCondition.RAIN_LIGHT,
            WeatherCondition.RAIN_MODERATE, WeatherCondition.RAIN_HEAVY,
            WeatherCondition.STORM
        )
        if (isRain) {
            listOf(0.32f, 0.48f, 0.64f).forEach { rx ->
                s.drawLine(
                    color = rain,
                    start = Offset(w * rx, h * 0.80f),
                    end = Offset(w * (rx - 0.05f), h * 0.93f),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
            }
        }
        if (cond == WeatherCondition.STORM) {
            val path = Path().apply {
                moveTo(w * 0.54f, h * 0.72f)
                lineTo(w * 0.46f, h * 0.85f)
                lineTo(w * 0.54f, h * 0.85f)
                lineTo(w * 0.48f, h * 0.97f)
            }
            s.drawPath(path = path, color = bolt)
        }
    }
}
