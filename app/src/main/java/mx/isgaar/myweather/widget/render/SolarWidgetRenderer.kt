// Location: app/.../widget/render/SolarWidgetRenderer.kt
// Function: Renders the 2x2 solar horizon widget inspired by Design 3 with
// sunset headline, sine-wave horizon curve, glowing sun dot, and sunrise clock.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.SolarMath
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.sin

object SolarWidgetRenderer {

    fun render(
        ctx: Context,
        snap: WeatherSnapshot?,
        wDp: Int,
        hDp: Int
    ): Bitmap {
        val (bmp, canvas) = WidgetCanvasHelper.createBitmap(ctx, wDp, hDp)
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val top = ContextCompat.getColor(ctx, R.color.widget_slate_top)
        val bot = ContextCompat.getColor(ctx, R.color.widget_slate_bottom)
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val sun = ContextCompat.getColor(ctx, R.color.accent_sun)
        WidgetCanvasHelper.drawCardBackground(canvas, top, bot, w * 0.14f)

        val today = snap?.todayForecast()
        val riseIso = today?.sunriseIso.orEmpty()
        val setIso = today?.sunsetIso.orEmpty()
        val riseMin = SolarMath.parseIsoMinutes(riseIso) ?: (6 * 60 + 34)
        val setMin = SolarMath.parseIsoMinutes(setIso) ?: (17 * 60 + 51)
        val now = LocalTime.now()
        val nowMin = now.hour * 60 + now.minute
        val prog = SolarMath.dayProgress(nowMin, riseMin, setMin)

        val pad = w * 0.10f
        canvas.drawText(
            ctx.getString(R.string.sunset_label).uppercase(),
            pad,
            pad + h * 0.08f,
            WidgetCanvasHelper.textPaint(sec, h * 0.085f, bold = true)
        )
        canvas.drawText(
            SolarMath.formatIsoClock(setIso),
            pad,
            pad + h * 0.25f,
            WidgetCanvasHelper.textPaint(white, h * 0.18f, bold = true)
        )

        val horizonY = h * 0.67f
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x66FFFFFF
            strokeWidth = 2f
        }
        canvas.drawLine(0f, horizonY, w, horizonY, linePaint)

        val arcPath = Path()
        for (i in 0..48) {
            val t = i / 48f
            val x = w * t
            val y = horizonY - sin(t * PI).toFloat() * (h * 0.19f)
            if (i == 0) arcPath.moveTo(x, y) else arcPath.lineTo(x, y)
        }
        val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = sec
            style = Paint.Style.STROKE
            strokeWidth = 4f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawPath(arcPath, arcPaint)

        val dotX = w * prog
        val dotY = horizonY - sin(prog * PI).toFloat() * (h * 0.19f)
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = sun and 0x55FFFFFF
        }
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = white
        }
        canvas.drawCircle(dotX, dotY, w * 0.06f, glowPaint)
        canvas.drawCircle(dotX, dotY, w * 0.025f, dotPaint)

        val riseText = ctx.getString(
            R.string.sunrise_value_fmt,
            SolarMath.formatIsoClock(riseIso)
        )
        canvas.drawText(
            riseText,
            pad,
            h - pad * 0.7f,
            WidgetCanvasHelper.textPaint(sec, h * 0.085f)
        )
        return bmp
    }
}
