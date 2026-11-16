// Location: app/.../widget/render/MinimalWidgetRenderer.kt
// Function: Renders the frosted light minimal widget with high-contrast glyphs
// and responsive layouts across compact, square, and wide widget grids.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.widget.data.WidgetTextHelper
import kotlin.math.min

object MinimalWidgetRenderer {

    fun render(
        ctx: Context, snap: WeatherSnapshot?,
        unit: TemperatureUnit, wDp: Int, hDp: Int
    ): Bitmap {
        val (bmp, canvas) = WidgetCanvasHelper.createBitmap(ctx, wDp, hDp)
        val w = canvas.width.toFloat()
        val u = min(w, canvas.height.toFloat())
        WidgetCanvasHelper.drawCardBackground(
            canvas,
            ContextCompat.getColor(ctx, R.color.widget_light_card),
            ContextCompat.getColor(ctx, R.color.widget_light_card_bottom),
            (u * 0.13f).coerceAtMost(w * 0.14f),
            ContextCompat.getColor(ctx, R.color.widget_light_border)
        )
        val fg = ContextCompat.getColor(ctx, R.color.widget_light_text)
        val sub = ContextCompat.getColor(ctx, R.color.widget_light_subtext)
        val cond = snap?.current?.condition ?: WeatherCondition.CLEAR
        val temp = snap?.let {
            UnitFormatter.formatTemp(it.current.temperatureC, unit)
        } ?: "--"
        if (w > canvas.height * 1.32f) {
            drawWide(ctx, canvas, snap, cond, temp, fg, sub)
        } else {
            drawSquare(ctx, canvas, snap, cond, temp, fg, sub)
        }
        return bmp
    }

    private fun drawWide(
        ctx: Context, canvas: Canvas, snap: WeatherSnapshot?,
        cond: WeatherCondition, temp: String, fg: Int, sub: Int
    ) {
        val (w, h) = canvas.width.toFloat() to canvas.height.toFloat()
        val u = min(w, h)
        val pad = u * 0.09f
        val refR = (u * 0.036f).coerceIn(10f, 18f)
        val gSz = u * 0.30f
        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, snap?.current?.isDay ?: true,
            pad + gSz * 0.50f, h * 0.48f, gSz, true
        )
        val tp = WidgetCanvasHelper::textPaint
        val fit = WidgetTextHelper::fit
        val tPaint = fit(tp(fg, u * 0.30f, false, true), temp, w * 0.25f, 12f)
        val tX = pad + gSz * 1.08f
        canvas.drawText(temp, tX, h * 0.59f, tPaint)
        val rX = maxOf(w * 0.56f, tX + tPaint.measureText(temp) + u * 0.12f)
        val rMax = (w - rX - pad).coerceAtLeast(u * 0.3f)
        val cp = tp(fg, u * 0.125f, true, false)
        val city = WidgetTextHelper.ellipsize(
            cp, snap?.location?.name ?: "Orizaba", rMax
        )
        canvas.drawText(city, rX, h * 0.38f, cp)
        val cStr = ctx.getString(cond.labelRes)
        val sp = fit(tp(sub, u * 0.095f, false, false), cStr, rMax, 12f)
        canvas.drawText(cStr, rX, h * 0.56f, sp)
        canvas.drawText(snap?.updatedLabel ?: "--:--", rX, h * 0.76f, sp)
        WidgetTextHelper.drawRefreshGlyph(
            canvas, w - pad - refR, h - pad - refR * 0.5f, refR, sub
        )
    }

    private fun drawSquare(
        ctx: Context, canvas: Canvas, snap: WeatherSnapshot?,
        cond: WeatherCondition, temp: String, fg: Int, sub: Int
    ) {
        val (w, h) = canvas.width.toFloat() to canvas.height.toFloat()
        val u = min(w, h)
        val refR = (u * 0.036f).coerceIn(10f, 18f)
        val tp = WidgetCanvasHelper::textPaint
        val fit = WidgetTextHelper::fit
        val gSz = u * 0.24f
        val tP = fit(tp(fg, u * 0.25f, false, true), temp, w * 0.48f, 12f)
        val startX = (w - gSz - u * 0.04f - tP.measureText(temp)) * 0.5f
        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, snap?.current?.isDay ?: true,
            startX + gSz * 0.48f, h * 0.34f, gSz, true
        )
        canvas.drawText(temp, startX + gSz + u * 0.04f, h * 0.42f, tP)
        val cMax = w * 0.84f
        val cp = tp(fg, u * 0.105f, true, false)
            .apply { textAlign = Paint.Align.CENTER }
        val city = snap?.location?.name ?: "Orizaba"
        canvas.drawText(city, w * 0.5f, h * 0.61f, fit(cp, city, cMax, 12f))
        val cStr = ctx.getString(cond.labelRes)
        val sp = tp(sub, u * 0.082f, false, false)
            .apply { textAlign = Paint.Align.CENTER }
        canvas.drawText(cStr, w * 0.5f, h * 0.74f, fit(sp, cStr, cMax, 12f))
        val stamp = snap?.updatedLabel ?: "--:--"
        val stP = tp(sub, u * 0.075f, false, false)
        val footW = stP.measureText(stamp) + u * 0.04f + refR * 2f
        val fX = (w - footW) * 0.5f
        canvas.drawText(stamp, fX, h - u * 0.10f, stP)
        WidgetTextHelper.drawRefreshGlyph(
            canvas, fX + footW - refR, h - u * 0.125f, refR, sub
        )
    }
}
