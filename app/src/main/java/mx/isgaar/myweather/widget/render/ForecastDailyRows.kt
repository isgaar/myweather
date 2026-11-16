// Location: app/.../widget/render/ForecastDailyRows.kt
// Function: Draws responsive multi-day forecast rows and thermal range bars
// adapting row count and typography cleanly inside expanded widget bounds.
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
import mx.isgaar.myweather.widget.data.WidgetDrawPrimitives
import mx.isgaar.myweather.widget.data.WidgetTextHelper
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
        h: Float,
        hDp: Int
    ) {
        val maxRows = if (hDp >= 290) 5 else 4
        val days = snap.daily.filter { it.dayIndexOffset >= 0 }.take(maxRows)
        if (days.isEmpty()) return
        val gMin = days.minOf { it.tempMinC }
        val gMax = days.maxOf { it.tempMaxC }
        val span = (gMax - gMin).coerceAtLeast(1.0)
        val white = ContextCompat.getColor(ctx, R.color.text_primary)
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        val cool = 0xFF7BE09C.toInt()
        val warm = 0xFFF5C443.toInt()
        val availH = (h - startY - pad * 0.9f).coerceAtLeast(40f)
        val rowH = availH / days.size
        val fontSz = (rowH * 0.52f).coerceAtMost(h * 0.044f)
        val barL = w * 0.45f
        val barR = w * 0.80f
        val barW = barR - barL

        days.forEachIndexed { idx, d ->
            val y = startY + idx * rowH + fontSz
            val dayName = if (d.dayIndexOffset == 0) {
                ctx.getString(R.string.today_label)
            } else {
                runCatching {
                    LocalDate.parse(d.isoDate).dayOfWeek
                        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                }.getOrDefault(d.isoDate.takeLast(2))
            }
            val nameP = WidgetTextHelper.fit(
                WidgetCanvasHelper.textPaint(white, fontSz, bold = true),
                dayName, w * 0.22f - pad
            )
            canvas.drawText(dayName, pad, y, nameP)
            val glyphSz = (rowH * 0.46f).coerceAtMost(w * 0.06f)
            WidgetCanvasHelper.drawWeatherGlyph(
                ctx, canvas, d.condition, true,
                w * 0.28f, y - fontSz * 0.35f, glyphSz
            )
            canvas.drawText(
                UnitFormatter.formatTemp(d.tempMinC, unit),
                w * 0.35f, y, WidgetCanvasHelper.textPaint(sec, fontSz * 0.95f)
            )
            val trackP = WidgetDrawPrimitives.paint(0x45091220)
            val topB = y - fontSz * 0.48f
            val botB = topB + (rowH * 0.18f).coerceIn(5f, 12f)
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
                UnitFormatter.formatTemp(d.tempMaxC, unit), w * 0.84f, y,
                WidgetCanvasHelper.textPaint(white, fontSz * 0.96f, bold = true)
            )
        }
    }
}
