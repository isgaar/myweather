// Location: app/.../widget/material/MaterialYouWide.kt
// Function: Renders the wide two-column Material You layout and shared
// expressive tonal pill chip helper using dynamic Monet surface tokens.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.material

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.widget.data.WidgetDrawPrimitives
import mx.isgaar.myweather.widget.data.WidgetTextHelper
import mx.isgaar.myweather.widget.render.WidgetCanvasHelper
import kotlin.math.min

object MaterialYouWide {

    fun ink(c: Int, s: Float, b: Boolean): Paint =
        WidgetCanvasHelper.textPaint(c, s, b).apply { clearShadowLayer() }

    fun drawPill(
        canvas: Canvas, x: Float, y: Float, maxW: Float, h: Float,
        txt: String, pal: YouPalette
    ) {
        val base = ink(pal.onChip, h * 0.48f, true)
        val p = WidgetTextHelper.fit(base, txt, maxW - h)
        val w = (p.measureText(txt) + h * 0.9f).coerceAtMost(maxW)
        val chipP = WidgetDrawPrimitives.paint(pal.chipBg)
        val rf = RectF(x, y, x + w, y + h)
        canvas.drawRoundRect(rf, h * 0.5f, h * 0.5f, chipP)
        canvas.drawText(txt, x + h * 0.45f, y + h * 0.66f, p)
    }

    fun draw(
        ctx: Context, canvas: Canvas, snap: WeatherSnapshot?,
        unit: TemperatureUnit, pal: YouPalette, lightSurf: Boolean
    ) {
        val (w, h) = canvas.width.toFloat() to canvas.height.toFloat()
        val u = min(w, h)
        val pad = u * 0.10f
        val cond = snap?.current?.condition ?: WeatherCondition.CLEAR
        val temp = snap?.let {
            UnitFormatter.formatTemp(it.current.temperatureC, unit)
        } ?: "--"
        val bR = u * 0.19f
        val (bCx, bCy) = (pad + bR) to (pad + bR * 0.95f)
        canvas.drawCircle(bCx, bCy, bR, WidgetDrawPrimitives.paint(pal.badgeBg))
        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, snap?.current?.isDay ?: true,
            bCx, bCy, bR * 1.15f, lightSurf
        )
        val tX = bCx + bR + u * 0.07f
        val lRight = w * 0.52f
        val tp = WidgetTextHelper.fit(
            ink(pal.fg, u * 0.30f, true), temp, (lRight - tX).coerceAtLeast(40f)
        )
        canvas.drawText(temp, tX, bCy + u * 0.11f, tp)
        drawPill(
            canvas, pad, h - pad - u * 0.16f, lRight - pad, u * 0.16f,
            ctx.getString(cond.labelRes), pal
        )
        val rBox = RectF(lRight + u * 0.06f, pad, w - pad, h - pad)
        val boxR = u * 0.14f
        val boxP = WidgetDrawPrimitives.paint(pal.badgeBg)
        canvas.drawRoundRect(rBox, boxR, boxR, boxP)
        val rx = rBox.left + u * 0.09f
        val rMax = rBox.width() - u * 0.18f
        val city = snap?.location?.name ?: "Orizaba"
        val cp = WidgetTextHelper.fit(ink(pal.fg, u * 0.11f, true), city, rMax)
        canvas.drawText(city, rx, rBox.top + u * 0.17f, cp)
        val wind = "${ctx.getString(R.string.wind_label)}: " +
            "${snap?.current?.windSpeedKmh ?: 0} km/h"
        val wBase = ink(pal.sub, u * 0.082f, false)
        val wp = WidgetTextHelper.fit(wBase, wind, rMax)
        canvas.drawText(wind, rx, rBox.centerY() + u * 0.04f, wp)
        val refR = (u * 0.030f).coerceIn(8f, 13f)
        canvas.drawText(
            snap?.updatedLabel ?: "--:--", rx, rBox.bottom - u * 0.09f,
            ink(pal.sub, u * 0.076f, false)
        )
        WidgetTextHelper.drawRefreshGlyph(
            canvas, rBox.right - u * 0.1f, rBox.bottom - u * 0.11f,
            refR, pal.sub
        )
    }
}