// Location: app/.../widget/render/WidgetCanvasHelper.kt
// Function: Shared Canvas helpers for crisp bitmap allocation, card gradients,
// and layered weather glyph orchestration on light or dark backgrounds.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.widget.data.WidgetDrawPrimitives

object WidgetCanvasHelper {

    fun createBitmap(ctx: Context, wDp: Int, hDp: Int): Pair<Bitmap, Canvas> {
        val d = ctx.resources.displayMetrics.density.coerceIn(1.5f, 2.25f)
        val pxW = (wDp.coerceIn(72, 540) * d).toInt()
        val pxH = (hDp.coerceIn(72, 540) * d).toInt()
        val bmp = Bitmap.createBitmap(pxW, pxH, Bitmap.Config.ARGB_8888)
        return bmp to Canvas(bmp)
    }

    fun drawCardBackground(
        canvas: Canvas,
        top: Int,
        bot: Int,
        r: Float,
        borderCol: Int = 0x33FFFFFF
    ) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val grad = LinearGradient(
            0f, 0f, 0f, h, top, bot, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(
            RectF(0f, 0f, w, h), r, r,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = grad }
        )
        val b = WidgetDrawPrimitives.paint(borderCol).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(RectF(1f, 1f, w - 1f, h - 1f), r, r, b)
    }

    fun textPaint(
        color: Int, sizePx: Float, bold: Boolean = false, light: Boolean = false
    ): Paint = WidgetDrawPrimitives.paint(color).apply {
        textSize = sizePx.coerceAtLeast(12f)
        typeface = when {
            light -> Typeface.create("sans-serif-light", Typeface.NORMAL)
            bold -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            else -> Typeface.DEFAULT
        }
    }

    fun drawWeatherGlyph(
        ctx: Context,
        canvas: Canvas,
        cond: WeatherCondition,
        isDay: Boolean,
        cx: Float,
        cy: Float,
        sz: Float,
        lightSurface: Boolean = false
    ) {
        val hasC = cond != WeatherCondition.CLEAR &&
            cond != WeatherCondition.MOSTLY_CLEAR
        if (!hasC || cond == WeatherCondition.PARTLY_CLOUDY) {
            val ox = if (hasC) sz * 0.20f else 0f
            val oy = if (hasC) -sz * 0.16f else 0f
            val rad = sz * (if (hasC) 0.25f else 0.34f)
            WidgetDrawPrimitives.drawCelestial(
                ctx, canvas, isDay, cx + ox, cy + oy, rad
            )
        }
        if (hasC) {
            WidgetDrawPrimitives.drawCloud(
                ctx, canvas, cx, cy, sz, lightSurface
            )
        }
        WidgetDrawPrimitives.drawPrecip(
            ctx, canvas, cond, cx, cy, sz, lightSurface
        )
    }
}
