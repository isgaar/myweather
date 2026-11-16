// Location: app/.../widget/data/DynamicWideLayout.kt
// Function: Draws the wide two-column responsive layout for Dynamic Weather
// widgets resized to 3x2 or 4x2 slots with auto-fitted stats and glyphs.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.data

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.widget.render.WidgetCanvasHelper
import kotlin.math.min

object DynamicWideLayout {

    fun draw(
        ctx: Context,
        canvas: Canvas,
        snap: WeatherSnapshot?,
        unit: TemperatureUnit,
        pad: Float
    ) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val u = min(w, h)
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val cond = snap?.current?.condition ?: WeatherCondition.CLEAR
        val isDay = snap?.current?.isDay ?: true
        val today = snap?.todayForecast()
        val temp = snap?.let {
            UnitFormatter.formatTemp(it.current.temperatureC, unit)
        } ?: "--"
        val city = snap?.location?.name ?: "Orizaba"
        val hl = today?.let {
            "${UnitFormatter.formatTemp(it.tempMaxC, unit)} / " +
                UnitFormatter.formatTemp(it.tempMinC, unit)
        }.orEmpty()
        val precip = today?.let {
            ctx.getString(R.string.precip_none_fmt, it.precipitationSumMm)
        }.orEmpty()
        val wind = "${ctx.getString(R.string.wind_label)}: " +
            "${snap?.current?.windSpeedKmh ?: 0} km/h"

        val lMax = w * 0.50f - pad * 1.5f
        val cityP = WidgetTextHelper.fit(
            WidgetCanvasHelper.textPaint(white, u * 0.115f, bold = true),
            city, lMax - u * 0.08f
        )
        canvas.drawText(city, pad, pad + u * 0.08f, cityP)
        WidgetTextHelper.drawLocationArrow(
            canvas, pad + cityP.measureText(city) + u * 0.06f,
            pad + u * 0.045f, u * 0.055f, white
        )
        val tP = WidgetTextHelper.fit(
            WidgetCanvasHelper.textPaint(white, u * 0.34f, light = true),
            temp, lMax
        )
        canvas.drawText(temp, pad, h * 0.62f, tP)
        val subLine = "${ctx.getString(cond.labelRes)}  $hl"
        val sP = WidgetTextHelper.fit(
            WidgetCanvasHelper.textPaint(sec, u * 0.090f), subLine, lMax
        )
        canvas.drawText(subLine, pad, h - pad * 0.75f, sP)

        val rX = w * 0.56f
        val rMax = w - rX - pad
        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, isDay, w - pad - u * 0.14f,
            pad + u * 0.15f, u * 0.25f
        )
        val statP = WidgetTextHelper.fit(
            WidgetCanvasHelper.textPaint(sec, u * 0.088f), precip, rMax
        )
        canvas.drawText(precip, rX, h * 0.57f, statP)
        val wP = WidgetTextHelper.fit(
            WidgetCanvasHelper.textPaint(sec, u * 0.088f), wind, rMax
        )
        canvas.drawText(wind, rX, h * 0.72f, wP)
        canvas.drawText(
            snap?.updatedLabel ?: "--:--", rX, h - pad * 0.75f, statP
        )
    }
}

