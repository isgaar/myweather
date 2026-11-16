// Location: app/.../widget/render/WidgetCanvasHelper.kt
// Function: Shared Canvas helpers for Inter typography, translucent frosted
// card gradients, ambient dark drop shadows, and layered weather glyphs.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.widget.data.WidgetDrawPrimitives

object WidgetCanvasHelper {

    private var fLight: Typeface? = null
    private var fReg: Typeface? = null
    private var fSemi: Typeface? = null

    private fun initFonts(ctx: Context) {
        if (fReg != null) return
        val build = { w: Int ->
            runCatching {
                Typeface.Builder(ctx.assets, "fonts/inter_var.ttf")
                    .setFontVariationSettings("'wght' $w").build()
            }.getOrNull()
        }
        val regF = ResourcesCompat.getFont(ctx, R.font.inter_regular)
        val semiF = ResourcesCompat.getFont(ctx, R.font.inter_semibold)
        fLight = build(340) ?: regF
        fReg = build(420) ?: regF
        fSemi = build(600) ?: semiF
    }

    fun createBitmap(ctx: Context, wDp: Int, hDp: Int): Pair<Bitmap, Canvas> {
        initFonts(ctx)
        val d = ctx.resources.displayMetrics.density.coerceIn(1.5f, 2.25f)
        val pxW = (wDp.coerceIn(72, 540) * d).toInt()
        val pxH = (hDp.coerceIn(72, 540) * d).toInt()
        val bmp = Bitmap.createBitmap(pxW, pxH, Bitmap.Config.ARGB_8888)
        return bmp to Canvas(bmp)
    }

    fun drawCardBackground(
        canvas: Canvas, top: Int, bot: Int, r: Float,
        borderCol: Int = 0x3DFFFFFF
    ) {
        val (w, h) = canvas.width.toFloat() to canvas.height.toFloat()
        val m = 4f
        val rf = RectF(m, m, w - m, h - m)
        val sh = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x18000000; setShadowLayer(8f, 0f, 3f, 0x62040A18)
        }
        canvas.drawRoundRect(rf, r, r, sh)
        canvas.save()
        canvas.clipPath(
            Path().apply { addRoundRect(rf, r, r, Path.Direction.CW) }
        )
        val tile = Shader.TileMode.CLAMP
        val gp = Paint(Paint.ANTI_ALIAS_FLAG)
        val s1 = LinearGradient(0f, m, 0f, h - m, top, bot, tile)
        val s2 = RadialGradient(w * .25f, h * .1f, w * .8f, 0x20FFFFFF, 0, tile)
        val s3 = LinearGradient(0f, h * .44f, 0f, h - m, 0, 0x34050B18, tile)
        for (s in listOf(s1, s2, s3)) {
            gp.shader = s; canvas.drawRect(rf, gp)
        }
        canvas.restore()
        val b = WidgetDrawPrimitives.paint(borderCol).apply {
            style = Paint.Style.STROKE; strokeWidth = 1.8f
        }
        rf.inset(1f, 1f)
        canvas.drawRoundRect(rf, r, r, b)
    }

    fun textPaint(
        color: Int, sizePx: Float, bold: Boolean = false, light: Boolean = false
    ): Paint = WidgetDrawPrimitives.paint(color).apply {
        val sz = sizePx.coerceAtLeast(12f)
        textSize = sz
        typeface = when {
            light -> fLight ?: Typeface.DEFAULT
            bold -> fSemi ?: Typeface.DEFAULT_BOLD
            else -> fReg ?: Typeface.DEFAULT
        }
        letterSpacing = if (light) -0.025f else -0.01f
        fontFeatureSettings = "tnum"
        setShadowLayer(
            (sz * 0.14f).coerceIn(2f, 9f),
            0f, (sz * 0.05f).coerceIn(1f, 3.5f), 0x62040A18
        )
    }

    fun drawWeatherGlyph(
        ctx: Context, canvas: Canvas, cond: WeatherCondition, isDay: Boolean,
        cx: Float, cy: Float, sz: Float, lightSurface: Boolean = false
    ) {
        val dp = WidgetDrawPrimitives
        val hasC = cond != WeatherCondition.CLEAR &&
                cond != WeatherCondition.MOSTLY_CLEAR
        if (!hasC || cond == WeatherCondition.PARTLY_CLOUDY) {
            val ox = if (hasC) sz * 0.20f else 0f
            val oy = if (hasC) -sz * 0.16f else 0f
            val rad = sz * (if (hasC) 0.25f else 0.34f)
            dp.drawCelestial(ctx, canvas, isDay, cx + ox, cy + oy, rad)
        }
        if (hasC) dp.drawCloud(ctx, canvas, cx, cy, sz, lightSurface)
        dp.drawPrecip(ctx, canvas, cond, cx, cy, sz, lightSurface)
    }
}