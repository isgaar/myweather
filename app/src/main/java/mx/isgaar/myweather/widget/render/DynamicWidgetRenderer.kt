// Location: app/.../widget/render/DynamicWidgetRenderer.kt
// Function: Renders the responsive atmospheric widget across narrow, square,
// and wide home screen grids with multi-tone sky gradients and fitted stats.
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
import mx.isgaar.myweather.widget.data.DynamicWideLayout
import mx.isgaar.myweather.widget.data.WidgetTextHelper
import kotlin.math.min

object DynamicWidgetRenderer {

    fun render(
        ctx: Context, snap: WeatherSnapshot?,
        unit: TemperatureUnit, wDp: Int, hDp: Int
    ): Bitmap {
        val (bmp, canvas) = WidgetCanvasHelper.createBitmap(ctx, wDp, hDp)
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val u = min(w, h)
        val isDay = snap?.current?.isDay ?: true
        val topId = if (isDay) R.color.widget_sky_blue
        else R.color.sky_night_top
        val botId = if (isDay) R.color.widget_sky_blue_deep
        else R.color.sky_night_bottom
        WidgetCanvasHelper.drawCardBackground(
            canvas,
            ContextCompat.getColor(ctx, topId),
            ContextCompat.getColor(ctx, botId),
            (u * 0.13f).coerceAtMost(w * 0.14f)
        )
        val pad = (w * 0.085f).coerceAtLeast(u * 0.085f)
        if (w > h * 1.32f) {
            DynamicWideLayout.draw(ctx, canvas, snap, unit, pad)
        } else {
            drawCompact(ctx, canvas, snap, unit, pad)
        }
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val refR = (u * 0.038f).coerceIn(10f, 18f)
        WidgetTextHelper.drawRefreshGlyph(
            canvas, w - pad - refR * 0.5f, h - pad * 0.65f - refR * 0.2f,
            refR, white
        )
        return bmp
    }

    private fun drawCompact(
        ctx: Context, canvas: Canvas, snap: WeatherSnapshot?,
        unit: TemperatureUnit, pad: Float
    ) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val u = min(w, h)
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val cond = snap?.current?.condition ?: WeatherCondition.CLEAR
        val isDay = snap?.current?.isDay ?: true
        val today = snap?.todayForecast()
        val tp = WidgetCanvasHelper::textPaint
        val fit = WidgetTextHelper::fit
        val rowMax = w - pad * 2f
        val temp = snap?.let {
            UnitFormatter.formatTemp(it.current.temperatureC, unit)
        } ?: "--"
        canvas.drawText(
            temp, pad, pad + u * 0.23f,
            fit(tp(white, u * 0.27f, false, true), temp, rowMax * 0.56f, 12f)
        )
        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, isDay, w - pad - u * 0.12f,
            pad + u * 0.12f, u * 0.21f
        )
        today?.let { d ->
            val hl = "${UnitFormatter.formatTemp(d.tempMaxC, unit)} / " +
                UnitFormatter.formatTemp(d.tempMinC, unit)
            val p = tp(sec, u * 0.082f, false, false).apply {
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText(
                hl, w - pad, pad + u * 0.30f, fit(p, hl, rowMax * 0.45f, 12f)
            )
            val pr = ctx.getString(
                R.string.precip_none_fmt, d.precipitationSumMm
            )
            val pp = fit(tp(sec, u * 0.078f, false, false), pr, rowMax, 12f)
            canvas.drawText(pr, pad, h * 0.54f, pp)
        }
        val wind = "${ctx.getString(R.string.wind_label)}: " +
            "${snap?.current?.windSpeedKmh ?: 0} km/h"
        val wp = fit(tp(sec, u * 0.078f, false, false), wind, rowMax, 12f)
        canvas.drawText(wind, pad, h * 0.65f, wp)
        val refR = (u * 0.038f).coerceIn(10f, 18f)
        val city = snap?.location?.name ?: "Orizaba"
        val cp = fit(
            tp(white, u * 0.095f, true, false), city, rowMax - refR * 3.2f, 12f
        )
        canvas.drawText(city, pad, h - pad - u * 0.085f, cp)
        canvas.drawText(
            snap?.updatedLabel ?: "--:--", pad, h - pad * 0.55f,
            tp(sec, u * 0.070f, false, false)
        )
    }
}
