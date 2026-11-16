// Location: app/.../widget/render/DynamicWidgetRenderer.kt
// Function: Renders the 2x2 dynamic atmospheric widget inspired by Designs 1
// and 3 with live temperature, high/low, precipitation, wind, and refresh icon.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter

object DynamicWidgetRenderer {

    fun render(
        ctx: Context,
        snap: WeatherSnapshot?,
        unit: TemperatureUnit,
        wDp: Int,
        hDp: Int
    ): Bitmap {
        val (bmp, canvas) = WidgetCanvasHelper.createBitmap(ctx, wDp, hDp)
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val isDay = snap?.current?.isDay ?: true
        val topCol = ContextCompat.getColor(
            ctx,
            if (isDay) R.color.widget_sky_blue else R.color.sky_night_top
        )
        val botCol = ContextCompat.getColor(
            ctx,
            if (isDay) {
                R.color.widget_sky_blue_deep
            } else {
                R.color.sky_night_bottom
            }
        )
        WidgetCanvasHelper.drawCardBackground(canvas, topCol, botCol, w * 0.14f)

        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val pad = w * 0.10f
        val tempText = snap?.let {
            UnitFormatter.formatTemp(it.current.temperatureC, unit)
        } ?: "--"
        val cityText = snap?.location?.name ?: "Orizaba"
        val cond = snap?.current?.condition ?: WeatherCondition.CLEAR
        val today = snap?.todayForecast()

        canvas.drawText(
            tempText,
            pad,
            pad + h * 0.25f,
            WidgetCanvasHelper.textPaint(white, h * 0.28f, light = true)
        )
        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, isDay,
            w - pad - w * 0.14f, pad + h * 0.14f, w * 0.22f
        )
        if (today != null) {
            val hi = UnitFormatter.formatTemp(today.tempMaxC, unit)
            val lo = UnitFormatter.formatTemp(today.tempMinC, unit)
            canvas.drawText(
                "$hi / $lo",
                w - pad - w * 0.30f,
                pad + h * 0.32f,
                WidgetCanvasHelper.textPaint(sec, h * 0.085f)
            )
            val precip = ctx.getString(
                R.string.precip_none_fmt, today.precipitationSumMm
            )
            canvas.drawText(
                precip,
                pad,
                h * 0.54f,
                WidgetCanvasHelper.textPaint(sec, h * 0.080f)
            )
        }
        val wind = snap?.current?.windSpeedKmh ?: 0
        canvas.drawText(
            "${ctx.getString(R.string.wind_label)}: $wind km/h",
            pad,
            h * 0.65f,
            WidgetCanvasHelper.textPaint(sec, h * 0.080f)
        )
        canvas.drawText(
            cityText,
            pad,
            h * 0.80f,
            WidgetCanvasHelper.textPaint(white, h * 0.095f, bold = true)
        )
        canvas.drawText(
            snap?.updatedLabel ?: "--:--",
            pad,
            h * 0.90f,
            WidgetCanvasHelper.textPaint(sec, h * 0.072f)
        )
        WidgetCanvasHelper.drawRefreshGlyph(
            canvas, w - pad * 0.8f, h - pad * 0.8f, w * 0.05f, white
        )
        return bmp
    }
}
