// Location: app/.../widget/render/SolarWidgetRenderer.kt
// Function: Renders the frosted solar horizon widget with Inter typography,
// horizon-split dark scrim, continuous wave curve, and glowing solar orb.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.SolarMath
import mx.isgaar.myweather.widget.data.WidgetDrawPrimitives
import mx.isgaar.myweather.widget.data.WidgetTextHelper
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min

object SolarWidgetRenderer {

    fun render(
        ctx: Context, snap: WeatherSnapshot?, wDp: Int, hDp: Int
    ): Bitmap {
        val (bmp, canvas) = WidgetCanvasHelper.createBitmap(ctx, wDp, hDp)
        val (w, h) = canvas.width.toFloat() to canvas.height.toFloat()
        val u = min(w, h)
        val cr = (u * 0.13f).coerceAtMost(w * 0.14f)
        WidgetCanvasHelper.drawCardBackground(
            canvas,
            ContextCompat.getColor(ctx, R.color.widget_sky_blue),
            ContextCompat.getColor(ctx, R.color.widget_sky_blue_deep), cr
        )
        val today = snap?.todayForecast()
        val riseIso = today?.sunriseIso.orEmpty()
        val setIso = today?.sunsetIso.orEmpty()
        val now = LocalTime.now()
        val prog = SolarMath.dayProgress(
            now.hour * 60 + now.minute,
            SolarMath.parseIsoMinutes(riseIso) ?: 395,
            SolarMath.parseIsoMinutes(setIso) ?: 1070
        )
        val wide = w > h * 1.32f
        val hY = h * 0.66f
        canvas.save()
        val rf = RectF(4f, 4f, w - 4f, h - 4f)
        canvas.clipPath(
            Path().apply { addRoundRect(rf, cr, cr, Path.Direction.CW) }
        )
        val scrim = WidgetDrawPrimitives.paint(0x46050D1C)
        canvas.drawRect(4f, hY, w - 4f, h - 4f, scrim)
        canvas.restore()
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val x0 = if (wide) w * 0.44f else 4f
        val x1 = if (wide) w - u * 0.06f else w - 4f
        drawArc(canvas, x0, x1, hY, u * 0.17f, prog, sec, white)
        val pad = (w * 0.095f).coerceAtLeast(u * 0.095f)
        val maxW = (if (wide) w * 0.42f else w) - pad * 1.85f
        val r = u * 0.024f
        WidgetTextHelper.drawHorizonIcon(canvas, pad, u * 0.165f, r, sec)
        val lbl = ctx.getString(R.string.sunset_label).uppercase()
        val tp = WidgetCanvasHelper::textPaint
        val lp = WidgetTextHelper.fit(
            tp(sec, u * 0.082f, true, false), lbl, maxW - r * 3.1f, 12f
        )
        canvas.drawText(lbl, pad + r * 3.1f, u * 0.18f, lp)
        WidgetTextHelper.drawSplitClock(
            canvas, SolarMath.formatIsoClock(setIso), pad, u * 0.37f, maxW,
            { sz -> tp(white, sz, true, false) }, u * 0.21f
        )
        val rc = SolarMath.formatIsoClock(riseIso).replace(" ", "")
        val rise = ctx.getString(R.string.sunrise_value_fmt, rc)
        val rp = WidgetTextHelper.fit(
            tp(sec, u * 0.082f, false, false), rise, maxW, 12f
        )
        canvas.drawText(rise, pad, h - u * 0.10f, rp)
        return bmp
    }

    private fun drawArc(
        canvas: Canvas, x0: Float, x1: Float, hY: Float,
        amp: Float, prog: Float, sec: Int, white: Int
    ) {
        canvas.drawLine(x0, hY, x1, hY, WidgetDrawPrimitives.paint(0x52FFFFFF))
        val span = x1 - x0
        val wave = Path()
        val yOf = { t: Float ->
            hY + cos(t * 2.0 * PI).toFloat() * amp * 0.78f - amp * 0.14f
        }
        wave.moveTo(x0, yOf(0f))
        for (i in 1..56) wave.lineTo(x0 + span * (i / 56f), yOf(i / 56f))
        val stP = WidgetDrawPrimitives.paint(0x38050C18).apply {
            style = Paint.Style.STROKE; strokeWidth = 4f
        }
        canvas.drawPath(wave, stP)
        canvas.save()
        canvas.clipRect(x0, 0f, x1, hY)
        canvas.drawPath(wave, stP.apply { color = sec })
        canvas.restore()
        val dt = 0.22f + prog * 0.56f
        val (dx, dy) = (x0 + span * dt) to yOf(dt)
        val dr = (amp * 0.15f).coerceIn(5f, 11f)
        val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                dx, dy, dr * 2.8f, 0x99FFFFFF.toInt(), 0, Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(dx, dy, dr * 2.8f, glow)
        canvas.drawCircle(dx, dy, dr, WidgetDrawPrimitives.paint(white))
    }
}