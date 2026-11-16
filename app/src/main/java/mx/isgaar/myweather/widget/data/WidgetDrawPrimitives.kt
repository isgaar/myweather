// Location: app/.../widget/data/WidgetDrawPrimitives.kt
// Function: Layered Canvas weather art primitives drawing golden sun halos,
// crescent moons, two-tone light/dark clouds, and precipitation streaks.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.data

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.WeatherCondition

object WidgetDrawPrimitives {

    fun paint(c: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = c }

    fun drawCelestial(
        ctx: Context, canvas: Canvas, isDay: Boolean,
        cx: Float, cy: Float, r: Float
    ) {
        val sunTop = ContextCompat.getColor(ctx, R.color.accent_sun)
        val sunBot = ContextCompat.getColor(ctx, R.color.accent_sun_warm)
        if (isDay) {
            val halo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    cx, cy, r * 1.45f,
                    sunTop and 0x55FFFFFF, 0, Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(cx, cy, r * 1.45f, halo)
            val core = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    cx - r * 0.25f, cy - r * 0.25f, r * 1.2f,
                    sunTop, sunBot, Shader.TileMode.CLAMP
                )
                setShadowLayer((r * 0.25f).coerceIn(2f, 8f), 0f, 2f, 0x48040A18)
            }
            canvas.drawCircle(cx, cy, r, core)
        } else {
            val moonCol = ContextCompat.getColor(ctx, R.color.accent_moon)
            val cut = Path().apply {
                addCircle(
                    cx + r * 0.42f, cy - r * 0.22f, r * 0.78f, Path.Direction.CW
                )
            }
            val crescent = Path().apply {
                addCircle(cx, cy, r * 0.9f, Path.Direction.CW)
                op(cut, Path.Op.DIFFERENCE)
            }
            val mp = paint(moonCol).apply {
                setShadowLayer((r * 0.28f).coerceIn(2f, 8f), 0f, 2f, 0x52040A18)
            }
            canvas.drawPath(crescent, mp)
        }
    }

    fun drawCloud(
        ctx: Context, canvas: Canvas, cx: Float, cy: Float,
        sz: Float, lightSurface: Boolean
    ) {
        val baseC = ContextCompat.getColor(
            ctx,
            if (lightSurface) R.color.widget_cloud_light_fill
            else R.color.widget_cloud_dark_shade
        )
        val topC = ContextCompat.getColor(
            ctx,
            if (lightSurface) R.color.widget_cloud_light_top
            else R.color.text_primary
        )
        val sc = if (lightSurface) 0x260E1728 else 0x56040A18
        val baseP = paint(baseC).apply {
            setShadowLayer((sz * 0.14f).coerceIn(2f, 9f), 0f, 3f, sc)
        }
        val topP = paint(topC)
        val rf = RectF(
            cx - sz * 0.40f, cy + sz * 0.02f, cx + sz * 0.40f, cy + sz * 0.28f
        )
        canvas.drawRoundRect(rf, sz * 0.13f, sz * 0.13f, baseP)
        canvas.drawCircle(cx - sz * 0.17f, cy + sz * 0.04f, sz * 0.22f, topP)
        canvas.drawCircle(cx + sz * 0.09f, cy - sz * 0.04f, sz * 0.28f, topP)
        canvas.drawCircle(cx + sz * 0.26f, cy + sz * 0.09f, sz * 0.18f, baseP)
    }

    fun drawPrecip(
        ctx: Context, canvas: Canvas, cond: WeatherCondition,
        cx: Float, cy: Float, sz: Float, lightSurface: Boolean
    ) {
        val wet = cond == WeatherCondition.DRIZZLE ||
            cond == WeatherCondition.RAIN_LIGHT ||
            cond == WeatherCondition.RAIN_MODERATE ||
            cond == WeatherCondition.RAIN_HEAVY ||
            cond == WeatherCondition.STORM
        if (!wet) return
        val rainC = ContextCompat.getColor(
            ctx,
            if (lightSurface) R.color.widget_rain_light_card
            else R.color.widget_rain_dark_card
        )
        val rp = paint(rainC).apply {
            strokeWidth = (sz * 0.075f).coerceAtLeast(2.2f)
            strokeCap = Paint.Cap.ROUND
        }
        listOf(-0.20f, 0f, 0.20f).forEach { rx ->
            val x = cx + sz * rx
            canvas.drawLine(
                x, cy + sz * 0.34f, x - sz * 0.07f, cy + sz * 0.50f, rp
            )
        }
    }
}
