// Location: app/.../widget/data/ForecastHeaderHelper.kt
// Function: Draws the responsive header summary and adaptive 3-to-6 column
// hourly strip for the resizable Forecast home screen widget.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.data

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.widget.render.WidgetCanvasHelper
import kotlin.math.min

object ForecastHeaderHelper {

    fun drawHeader(
        ctx: Context, canvas: Canvas, snap: WeatherSnapshot?,
        unit: TemperatureUnit, pad: Float, tall: Boolean
    ) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val u = min(w, h)
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val city = snap?.location?.name ?: "Orizaba"
        val temp = snap?.let {
            UnitFormatter.formatTemp(it.current.temperatureC, unit)
        } ?: "--"
        val cond = snap?.current?.condition ?: WeatherCondition.CLEAR
        val isDay = snap?.current?.isDay ?: true
        val lMax = w * 0.48f - pad
        val cSz = if (tall) h * 0.055f else u * 0.115f
        val tSz = if (tall) h * 0.135f else u * 0.25f
        val tp = WidgetCanvasHelper::textPaint
        canvas.drawText(
            city, pad, pad + cSz * 0.9f,
            WidgetTextHelper.fit(tp(white, cSz, true, false), city, lMax)
        )
        canvas.drawText(
            temp, pad, pad + cSz + tSz * 0.98f,
            WidgetTextHelper.fit(tp(white, tSz, false, true), temp, lMax)
        )
        val rMax = w * 0.44f - pad
        val rSz = if (tall) h * 0.044f else u * 0.095f
        val gSz = (if (tall) u * 0.09f else u * 0.15f).coerceAtMost(w * 0.09f)
        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, isDay, w - pad - gSz * 0.55f,
            pad + gSz * 0.45f, gSz
        )
        val condTxt = ctx.getString(cond.labelRes)
        val rPaint = { s: String ->
            WidgetTextHelper.fit(
                tp(sec, rSz, false, false).apply {
                    textAlign = Paint.Align.RIGHT
                },
                s, rMax
            )
        }
        val condY = pad + gSz + rSz * 1.05f
        canvas.drawText(condTxt, w - pad, condY, rPaint(condTxt))
        snap?.todayForecast()?.let { d ->
            val hi = UnitFormatter.formatTemp(d.tempMaxC, unit)
            val lo = UnitFormatter.formatTemp(d.tempMinC, unit)
            val hl = "H:$hi  L:$lo"
            canvas.drawText(hl, w - pad, pad + gSz + rSz * 2.25f, rPaint(hl))
        }
    }

    fun drawHours(
        ctx: Context, canvas: Canvas, snap: WeatherSnapshot?,
        unit: TemperatureUnit, pad: Float, wDp: Int, tall: Boolean
    ) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val u = min(w, h)
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val count = when {
            wDp < 190 -> 3; wDp < 250 -> 4; wDp < 310 -> 5; else -> 6
        }
        val hours = snap?.hourly?.take(count).orEmpty()
        val hBaseY = if (tall) h * 0.34f else h * 0.58f
        val hSpan = (w - pad * 2f) / count.coerceAtLeast(1)
        val sz = (if (tall) h * 0.04f else u * 0.088f)
            .coerceAtMost(hSpan * 0.30f)
        val cSub = WidgetCanvasHelper.textPaint(sec, sz).apply {
            textAlign = Paint.Align.CENTER
        }
        val cMain = WidgetCanvasHelper.textPaint(white, sz * 1.08f, bold = true)
            .apply { textAlign = Paint.Align.CENTER }
        hours.forEachIndexed { idx, pt ->
            val cx = pad + hSpan * idx + hSpan * 0.5f
            val lbl = if (idx == 0) ctx.getString(R.string.now_label)
            else pt.hourLabel.take(2) + "h"
            canvas.drawText(lbl, cx, hBaseY, cSub)
            WidgetCanvasHelper.drawWeatherGlyph(
                ctx, canvas, pt.condition, pt.isDay, cx,
                hBaseY + (if (tall) h * 0.055f else u * 0.145f),
                (hSpan * 0.42f).coerceAtMost(u * 0.14f)
            )
            canvas.drawText(
                UnitFormatter.formatTemp(pt.temperatureC, unit),
                cx, hBaseY + (if (tall) h * 0.125f else u * 0.295f), cMain
            )
        }
    }
}
