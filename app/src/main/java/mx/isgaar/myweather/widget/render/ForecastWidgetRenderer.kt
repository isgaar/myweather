// Location: app/.../widget/render/ForecastWidgetRenderer.kt
// Function: Renders the resizable 4x2 hourly and 4x4 multi-day forecast widget
// inspired by Design 3 with 6-hour strip and daily thermal range bars.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter

object ForecastWidgetRenderer {

    fun render(
        ctx: Context,
        snap: WeatherSnapshot?,
        unit: TemperatureUnit,
        wDp: Int,
        hDp: Int
    ): Bitmap {
        val effW = wDp.coerceAtLeast(310)
        val effH = hDp.coerceAtLeast(155)
        val tall = effH >= 230
        val (bmp, canvas) = WidgetCanvasHelper.createBitmap(ctx, effW, effH)
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val isDay = snap?.current?.isDay ?: true
        val topId = if (isDay) R.color.sky_day_top else R.color.widget_slate_top
        val botId = if (isDay) R.color.widget_sky_blue_deep
        else R.color.widget_slate_bottom
        WidgetCanvasHelper.drawCardBackground(
            canvas,
            ContextCompat.getColor(ctx, topId),
            ContextCompat.getColor(ctx, botId),
            w * 0.06f
        )

        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val pad = w * 0.055f
        val city = snap?.location?.name ?: "Orizaba"
        val temp = snap?.let {
            UnitFormatter.formatTemp(it.current.temperatureC, unit)
        } ?: "--"
        val cond = snap?.current?.condition ?: WeatherCondition.CLEAR
        val today = snap?.todayForecast()
        val cSz = h * (if (tall) 0.055f else 0.105f)
        val tSz = h * (if (tall) 0.13f else 0.25f)
        canvas.drawText(
            city, pad, pad + h * (if (tall) 0.05f else 0.11f),
            WidgetCanvasHelper.textPaint(white, cSz, bold = true)
        )
        canvas.drawText(
            temp, pad, pad + h * (if (tall) 0.165f else 0.35f),
            WidgetCanvasHelper.textPaint(white, tSz, light = true)
        )

        val rP = WidgetCanvasHelper.textPaint(
            sec, h * (if (tall) 0.048f else 0.09f)
        ).apply { textAlign = Paint.Align.RIGHT }
        canvas.drawText(
            ctx.getString(cond.labelRes), w - pad,
            pad + h * (if (tall) 0.11f else 0.23f), rP
        )
        if (today != null) {
            val hi = UnitFormatter.formatTemp(today.tempMaxC, unit)
            val lo = UnitFormatter.formatTemp(today.tempMinC, unit)
            canvas.drawText(
                "H:$hi  L:$lo", w - pad,
                pad + h * (if (tall) 0.165f else 0.35f), rP
            )
        }
        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, isDay, w - pad - w * 0.06f,
            pad + h * (if (tall) 0.04f else 0.09f), w * 0.09f
        )

        val hours = snap?.hourly?.take(6).orEmpty()
        val hBaseY = if (tall) h * 0.31f else h * 0.56f
        val hSpan = (w - pad * 2) / 6f
        val cSub = WidgetCanvasHelper.textPaint(
            sec, h * (if (tall) 0.042f else 0.08f)
        ).apply { textAlign = Paint.Align.CENTER }
        val cMain = WidgetCanvasHelper.textPaint(
            white, h * (if (tall) 0.046f else 0.09f), bold = true
        ).apply { textAlign = Paint.Align.CENTER }
        hours.forEachIndexed { idx, pt ->
            val cx = pad + hSpan * idx + hSpan * 0.5f
            val lbl = if (idx == 0) ctx.getString(R.string.now_label)
            else pt.hourLabel.take(2) + "h"
            canvas.drawText(lbl, cx, hBaseY, cSub)
            WidgetCanvasHelper.drawWeatherGlyph(
                ctx, canvas, pt.condition, pt.isDay, cx,
                hBaseY + h * (if (tall) 0.06f else 0.14f), w * 0.065f
            )
            canvas.drawText(
                UnitFormatter.formatTemp(pt.temperatureC, unit),
                cx, hBaseY + h * (if (tall) 0.135f else 0.30f), cMain
            )
        }
        if (tall && snap != null) {
            ForecastDailyRows.draw(
                ctx, canvas, snap, unit, pad, h * 0.51f, w, h
            )
        }
        WidgetCanvasHelper.drawRefreshGlyph(
            canvas, w - pad * 0.7f, h - pad * 0.7f, w * 0.022f, sec
        )
        return bmp
    }
}
