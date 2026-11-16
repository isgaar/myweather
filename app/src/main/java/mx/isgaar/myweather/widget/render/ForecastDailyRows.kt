// Location: app/.../widget/render/ForecastDailyRows.kt
// Function: Draws the 5-day forecast rows and gradient thermal range bars for
// the expanded 4x4 home screen forecast widget using Canvas primitives.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

object ForecastDailyRows {

    fun draw(
        ctx: Context,
        canvas: Canvas,
        snap: WeatherSnapshot,
        unit: TemperatureUnit,
        pad: Float,
        startY: Float,
        w: Float,
        h: Float
    ) {
        val days = snap.daily.filter { it.dayIndexOffset >= 0 }.take(5)
        if (days.isEmpty()) return
        val gMin = days.minOf { it.tempMinC }
        val gMax = days.maxOf { it.tempMaxC }
        val span = (gMax - gMin).coerceAtLeast(1.0)
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val cool = ContextCompat.getColor(ctx, R.color.accent_cool)
        val warm = ContextCompat.getColor(ctx, R.color.accent_warm)
        val rowH = h * 0.088f
        val barL = w * 0.44f
        val barR = w * 0.80f
        val barW = barR - barL

        days.forEachIndexed { idx, d ->
            val y = startY + idx * rowH
            val dayName = if (d.dayIndexOffset == 0) {
                ctx.getString(R.string.today_label)
            } else {
                runCatching {
                    LocalDate.parse(d.isoDate).dayOfWeek
                        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                }.getOrDefault(d.isoDate.takeLast(2))
            }
            val nameP = WidgetCanvasHelper.textPaint(
                white, h * 0.048f, bold = true
            )
            canvas.drawText(dayName, pad, y, nameP)
            WidgetCanvasHelper.drawWeatherGlyph(
                ctx, canvas, d.condition, true,
                w * 0.28f, y - h * 0.015f, w * 0.055f
            )
            canvas.drawText(
                UnitFormatter.formatTemp(d.tempMinC, unit),
                w * 0.35f,
                y,
                WidgetCanvasHelper.textPaint(sec, h * 0.045f)
            )
            val trackP = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0x33FFFFFF
            }
            val topB = y - h * 0.022f
            val botB = topB + h * 0.016f
            canvas.drawRoundRect(RectF(barL, topB, barR, botB), 8f, 8f, trackP)
            val sF = ((d.tempMinC - gMin) / span).toFloat().coerceIn(0f, 0.85f)
            val wF = ((d.tempMaxC - d.tempMinC) / span).toFloat()
                .coerceIn(0.15f, 1f - sF)
            val fL = barL + barW * sF
            val fR = fL + barW * wF
            val barP = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    fL, topB, fR, topB, cool, warm, Shader.TileMode.CLAMP
                )
            }
            canvas.drawRoundRect(RectF(fL, topB, fR, botB), 8f, 8f, barP)
            canvas.drawText(
                UnitFormatter.formatTemp(d.tempMaxC, unit),
                w * 0.84f,
                y,
                WidgetCanvasHelper.textPaint(white, h * 0.046f, bold = true)
            )
        }
    }
}
