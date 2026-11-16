// Location: app/.../widget/render/ForecastWidgetRenderer.kt
// Function: Renders the responsive forecast widget keeping native proportions
// across 3x2, 4x2, and 4x4 grids with adaptive hourly columns and daily bars.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.widget.data.ForecastHeaderHelper
import mx.isgaar.myweather.widget.data.WidgetDrawPrimitives
import mx.isgaar.myweather.widget.data.WidgetTextHelper
import kotlin.math.min

object ForecastWidgetRenderer {

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
        val u = min(w, h)
        val tall = hDp >= 220 || h > w * 0.72f
        val topId = if (tall) R.color.widget_slate_top
        else R.color.widget_sky_blue
        val botId = if (tall) R.color.widget_slate_bottom
        else R.color.widget_sky_blue_deep
        WidgetCanvasHelper.drawCardBackground(
            canvas,
            ContextCompat.getColor(ctx, topId),
            ContextCompat.getColor(ctx, botId),
            (u * 0.11f).coerceAtMost(w * 0.08f)
        )
        val pad = (u * 0.078f).coerceAtLeast(14f)
        ForecastHeaderHelper.drawHeader(ctx, canvas, snap, unit, pad, tall)
        val divP = WidgetDrawPrimitives.paint(0x28FFFFFF).apply {
            strokeWidth = 1.4f
        }
        if (tall) {
            canvas.drawLine(pad, h * 0.305f, w - pad, h * 0.305f, divP)
            canvas.drawLine(pad, h * 0.530f, w - pad, h * 0.530f, divP)
        }
        ForecastHeaderHelper.drawHours(ctx, canvas, snap, unit, pad, wDp, tall)
        val refR = (u * 0.028f).coerceIn(8f, 13f)
        if (tall && snap != null) {
            val endBot = h - pad * 0.85f - refR * 1.4f
            ForecastDailyRows.draw(
                ctx, canvas, snap, unit, pad, h * 0.545f, w, endBot, hDp
            )
        }
        val sec = ContextCompat.getColor(ctx, R.color.text_secondary)
        WidgetTextHelper.drawRefreshGlyph(
            canvas, w - pad - refR * 0.4f, h - pad * 0.72f - refR, refR, sec
        )
        return bmp
    }
}