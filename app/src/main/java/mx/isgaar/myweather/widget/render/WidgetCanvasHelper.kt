// Location: app/.../widget/render/WidgetCanvasHelper.kt
// Function: Shared Canvas drawing primitives for home screen widgets including
// rounded gradient cards, vector weather glyphs, and refresh indicators.
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
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.WeatherCondition

object WidgetCanvasHelper {

    fun createBitmap(ctx: Context, wDp: Int, hDp: Int): Pair<Bitmap, Canvas> {
        val d = ctx.resources.displayMetrics.density.coerceIn(1.5f, 3.0f)
        val pxW = (wDp.coerceIn(120, 460) * d).toInt()
        val pxH = (hDp.coerceIn(110, 460) * d).toInt()
        val bmp = Bitmap.createBitmap(pxW, pxH, Bitmap.Config.ARGB_8888)
        return bmp to Canvas(bmp)
    }

    fun drawCardBackground(canvas: Canvas, top: Int, bot: Int, r: Float) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val grad = LinearGradient(
            0f, 0f, 0f, h, top, bot, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(
            RectF(0f, 0f, w, h), r, r,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = grad }
        )
        val b = solidPaint(0x33FFFFFF).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(RectF(1f, 1f, w - 1f, h - 1f), r, r, b)
    }

    fun textPaint(
        color: Int, sizePx: Float, bold: Boolean = false, light: Boolean = false
    ): Paint = solidPaint(color).apply {
        textSize = sizePx
        typeface = when {
            light -> Typeface.create("sans-serif-light", Typeface.NORMAL)
            bold -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            else -> Typeface.DEFAULT
        }
    }

    private fun solidPaint(c: Int) =
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = c }

    fun drawWeatherGlyph(
        ctx: Context, canvas: Canvas, cond: WeatherCondition,
        isDay: Boolean, cx: Float, cy: Float, sz: Float
    ) {
        val sunC = ContextCompat.getColor(ctx, R.color.accent_sun)
        val moonC = ContextCompat.getColor(ctx, R.color.accent_moon)
        val rainC = ContextCompat.getColor(ctx, R.color.accent_rain)
        val cloudC = ContextCompat.getColor(ctx, R.color.text_primary)
        val hasC = cond != WeatherCondition.CLEAR &&
            cond != WeatherCondition.MOSTLY_CLEAR
        if (!hasC || cond == WeatherCondition.PARTLY_CLOUDY) {
            val cp = solidPaint(if (isDay) sunC else moonC)
            val ox = if (hasC) sz * 0.22f else 0f
            val oy = if (hasC) -sz * 0.16f else 0f
            canvas.drawCircle(
                cx + ox, cy + oy, sz * (if (hasC) 0.28f else 0.36f), cp
            )
        }
        if (hasC) {
            val cp = solidPaint(cloudC)
            canvas.drawCircle(cx - sz * 0.16f, cy + sz * 0.04f, sz * 0.22f, cp)
            canvas.drawCircle(cx + sz * 0.08f, cy - sz * 0.02f, sz * 0.27f, cp)
            val rf = RectF(
                cx - sz * 0.36f, cy + sz * 0.04f,
                cx + sz * 0.36f, cy + sz * 0.26f
            )
            canvas.drawRoundRect(rf, sz * 0.11f, sz * 0.11f, cp)
        }
        if (cond in listOf(
                WeatherCondition.DRIZZLE, WeatherCondition.RAIN_LIGHT,
                WeatherCondition.RAIN_MODERATE, WeatherCondition.RAIN_HEAVY,
                WeatherCondition.STORM
            )
        ) {
            val rp = solidPaint(rainC).apply {
                strokeWidth = (sz * 0.08f).coerceAtLeast(2.5f)
                strokeCap = Paint.Cap.ROUND
            }
            listOf(-0.18f, 0f, 0.18f).forEach { rx ->
                val x = cx + sz * rx
                canvas.drawLine(
                    x, cy + sz * 0.32f, x - sz * 0.06f, cy + sz * 0.48f, rp
                )
            }
        }
    }

    fun drawRefreshGlyph(
        canvas: Canvas, cx: Float, cy: Float, r: Float, col: Int
    ) {
        val p = solidPaint(col).apply {
            style = Paint.Style.STROKE
            strokeWidth = (r * 0.22f).coerceAtLeast(2.5f)
            strokeCap = Paint.Cap.ROUND
        }
        val box = RectF(cx - r, cy - r, cx + r, cy + r)
        canvas.drawArc(box, -30f, 285f, false, p)
    }
}
