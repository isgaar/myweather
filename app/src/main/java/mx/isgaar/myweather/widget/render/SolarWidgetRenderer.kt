// Location: app/.../widget/render/SolarWidgetRenderer.kt
// Function: Renders the responsive solar horizon widget across narrow, square,
// and wide grid sizes with auto-fitted clocks, sunlit curve fill, and dot.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.SolarMath
import mx.isgaar.myweather.widget.data.WidgetDrawPrimitives
import mx.isgaar.myweather.widget.data.WidgetTextHelper
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

object SolarWidgetRenderer {

    fun render(
        ctx: Context, snap: WeatherSnapshot?, wDp: Int, hDp: Int
    ): Bitmap {
        val (bmp, canvas) = WidgetCanvasHelper.createBitmap(ctx, wDp, hDp)
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val u = min(w, h)
        val top = ContextCompat.getColor(ctx, R.color.widget_slate_top)
        val bot = ContextCompat.getColor(ctx, R.color.widget_slate_bottom)
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val sun = ContextCompat.getColor(ctx, R.color.accent_sun)
        WidgetCanvasHelper.drawCardBackground(
            canvas, top, bot, (u * 0.13f).coerceAtMost(w * 0.14f)
        )
        val today = snap?.todayForecast()
        val riseIso = today?.sunriseIso.orEmpty()
        val setIso = today?.sunsetIso.orEmpty()
        val riseMin = SolarMath.parseIsoMinutes(riseIso) ?: (6 * 60 + 35)
        val setMin = SolarMath.parseIsoMinutes(setIso) ?: (17 * 60 + 50)
        val now = LocalTime.now()
        val prog = SolarMath.dayProgress(
            now.hour * 60 + now.minute, riseMin, setMin
        )
        val wide = w > h * 1.32f
        val pad = (w * 0.095f).coerceAtLeast(u * 0.095f)
        val txtMax = (if (wide) w * 0.44f else w) - pad * 1.9f
        val tp = WidgetCanvasHelper::textPaint
        val fit = WidgetTextHelper::fit
        val lbl = ctx.getString(R.string.sunset_label).uppercase()
        val lblP = fit(tp(sec, u * 0.088f, true, false), lbl, txtMax, 12f)
        canvas.drawText(lbl, pad, u * 0.18f, lblP)
        val setClock = SolarMath.formatIsoClock(setIso)
        canvas.drawText(
            setClock, pad, u * 0.38f,
            fit(tp(white, u * 0.21f, true, false), setClock, txtMax, 12f)
        )
        val riseTxt = ctx.getString(
            R.string.sunrise_value_fmt, SolarMath.formatIsoClock(riseIso)
        )
        canvas.drawText(
            riseTxt, pad, h - u * 0.10f,
            fit(tp(sec, u * 0.082f, false, false), riseTxt, txtMax, 12f)
        )
        val x0 = if (wide) w * 0.46f else 0f
        val x1 = if (wide) w - pad * 0.6f else w
        val horizonY = if (wide) h * 0.68f else h * 0.66f
        drawArc(canvas, x0, x1, horizonY, u * 0.20f, prog, sec, sun, white)
        return bmp
    }

    private fun drawArc(
        canvas: Canvas, x0: Float, x1: Float, horizonY: Float,
        amp: Float, prog: Float, sec: Int, sun: Int, white: Int
    ) {
        val lineP = WidgetDrawPrimitives.paint(0x55FFFFFF).apply {
            strokeWidth = 2f
        }
        canvas.drawLine(x0, horizonY, x1, horizonY, lineP)
        val span = x1 - x0
        val arcPath = Path()
        val fillPath = Path().apply { moveTo(x0, horizonY) }
        for (i in 0..48) {
            val t = i / 48f
            val x = x0 + span * t
            val y = horizonY - sin(t * PI).toFloat() * amp
            if (i == 0) arcPath.moveTo(x, y) else arcPath.lineTo(x, y)
            fillPath.lineTo(x, y)
        }
        fillPath.lineTo(x1, horizonY)
        fillPath.close()
        val fillP = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, horizonY - amp, 0f, horizonY,
                0x38FFCA28, 0x05FFCA28, Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(fillPath, fillP)
        val arcP = WidgetDrawPrimitives.paint(sec).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3.5f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawPath(arcPath, arcP)
        val dotX = x0 + span * prog
        val dotY = horizonY - sin(prog * PI).toFloat() * amp
        val dotR = (amp * 0.14f).coerceIn(5f, 12f)
        val glow = WidgetDrawPrimitives.paint(sun and 0x55FFFFFF)
        canvas.drawCircle(dotX, dotY, dotR * 2.4f, glow)
        canvas.drawCircle(dotX, dotY, dotR, WidgetDrawPrimitives.paint(white))
    }
}
