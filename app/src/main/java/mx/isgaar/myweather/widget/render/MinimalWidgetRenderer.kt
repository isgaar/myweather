// Location: app/.../widget/render/MinimalWidgetRenderer.kt
// Function: Renders the clean light minimal 2x2 widget inspired by Design 1
// featuring centered weather glyph, temperature, city label, and refresh icon.
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

object MinimalWidgetRenderer {

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
        val bg = ContextCompat.getColor(ctx, R.color.widget_light_card)
        val fg = ContextCompat.getColor(ctx, R.color.widget_light_text)
        val sub = ContextCompat.getColor(ctx, R.color.widget_light_subtext)
        WidgetCanvasHelper.drawCardBackground(canvas, bg, bg, w * 0.14f)

        val cond = snap?.current?.condition ?: WeatherCondition.CLEAR
        val isDay = snap?.current?.isDay ?: true
        val tempStr = snap?.let {
            UnitFormatter.formatTemp(it.current.temperatureC, unit)
        } ?: "--"
        val cityStr = snap?.location?.name ?: "Orizaba"
        val stamp = snap?.updatedLabel ?: "--:--"

        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, isDay, w * 0.30f, h * 0.40f, w * 0.24f
        )
        canvas.drawText(
            tempStr,
            w * 0.48f,
            h * 0.48f,
            WidgetCanvasHelper.textPaint(fg, h * 0.24f, light = true)
        )
        val centerPaint = WidgetCanvasHelper.textPaint(
            fg, h * 0.10f, bold = true
        ).apply {
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(cityStr, w * 0.50f, h * 0.69f, centerPaint)

        val subPaint = WidgetCanvasHelper.textPaint(sub, h * 0.080f).apply {
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            snap?.let { ctx.getString(it.current.condition.labelRes) }
                ?: ctx.getString(R.string.cond_clear),
            w * 0.50f,
            h * 0.80f,
            subPaint
        )
        canvas.drawText(stamp, w * 0.44f, h * 0.91f, subPaint)
        WidgetCanvasHelper.drawRefreshGlyph(
            canvas, w * 0.72f, h * 0.885f, w * 0.045f, sub
        )
        return bmp
    }
}
