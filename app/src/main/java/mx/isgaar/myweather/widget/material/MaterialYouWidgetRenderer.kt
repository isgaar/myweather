// Location: app/.../widget/material/MaterialYouWidgetRenderer.kt
// Function: Renders a responsive Material You widget driven by dynamic Monet
// wallpaper tokens, expressive rounded geometry, and tonal condition pills.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.material

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.widget.data.WidgetDrawPrimitives
import mx.isgaar.myweather.widget.data.WidgetTextHelper
import mx.isgaar.myweather.widget.render.WidgetCanvasHelper
import kotlin.math.min

object MaterialYouWidgetRenderer {

    fun render(
        ctx: Context, snap: WeatherSnapshot?,
        unit: TemperatureUnit, wDp: Int, hDp: Int
    ): Bitmap {
        val (bmp, canvas) = WidgetCanvasHelper.createBitmap(ctx, wDp, hDp)
        val (w, h) = canvas.width.toFloat() to canvas.height.toFloat()
        val u = min(w, h)
        val pal = MaterialYouPalette.resolve(ctx)
        val r = (u * 0.21f).coerceAtMost(w * 0.22f)
        drawSurface(canvas, pal, w, h, r)
        val lightSurf = !MaterialYouPalette.isNight(ctx)
        if (w > h * 1.32f) {
            MaterialYouWide.draw(ctx, canvas, snap, unit, pal, lightSurf)
        } else {
            drawSquare(ctx, canvas, snap, unit, pal, lightSurf)
        }
        return bmp
    }

    private fun drawSurface(
        canvas: Canvas, pal: YouPalette, w: Float, h: Float, r: Float
    ) {
        val m = 4f
        val rf = RectF(m, m, w - m, h - m)
        val sh = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x18000000; setShadowLayer(7f, 0f, 3f, 0x48050A14)
        }
        canvas.drawRoundRect(rf, r, r, sh)
        val grad = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, m, 0f, h - m, pal.surfaceTop, pal.surfaceBot,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rf, r, r, grad)
        val b = WidgetDrawPrimitives.paint(pal.border).apply {
            style = Paint.Style.STROKE; strokeWidth = 1.6f
        }
        rf.inset(1f, 1f)
        canvas.drawRoundRect(rf, r, r, b)
    }

    private fun drawSquare(
        ctx: Context, canvas: Canvas, snap: WeatherSnapshot?,
        unit: TemperatureUnit, pal: YouPalette, lightSurf: Boolean
    ) {
        val (w, h) = canvas.width.toFloat() to canvas.height.toFloat()
        val u = min(w, h)
        val pad = u * 0.105f
        val cond = snap?.current?.condition ?: WeatherCondition.CLEAR
        val isDay = snap?.current?.isDay ?: true
        val temp = snap?.let {
            UnitFormatter.formatTemp(it.current.temperatureC, unit)
        } ?: "--"
        val bR = u * 0.145f
        val (bCx, bCy) = (w - pad - bR) to (pad + bR)
        canvas.drawCircle(bCx, bCy, bR, WidgetDrawPrimitives.paint(pal.badgeBg))
        WidgetCanvasHelper.drawWeatherGlyph(
            ctx, canvas, cond, isDay, bCx, bCy, bR * 1.15f, lightSurf
        )
        val tMax = (bCx - bR - pad - u * 0.04f).coerceAtLeast(u * 0.35f)
        val ink = MaterialYouWide::ink
        val tp = WidgetTextHelper.fit(ink(pal.fg, u * 0.28f, true), temp, tMax)
        canvas.drawText(temp, pad, pad + u * 0.24f, tp)
        val hl = snap?.todayForecast()?.let { d ->
            " ${UnitFormatter.formatTemp(d.tempMaxC, unit)}/" +
                UnitFormatter.formatTemp(d.tempMinC, unit)
        }.orEmpty()
        val pillTxt = "${ctx.getString(cond.labelRes)}$hl"
        MaterialYouWide.drawPill(
            canvas, pad, h * 0.49f, w - pad * 2f, u * 0.155f, pillTxt, pal
        )
        val refR = (u * 0.032f).coerceIn(8f, 14f)
        val city = snap?.location?.name ?: "Orizaba"
        val cMax = w - pad * 2f - refR * 3f
        val cp = WidgetTextHelper.fit(ink(pal.fg, u * 0.102f, true), city, cMax)
        canvas.drawText(city, pad, h - pad - u * 0.085f, cp)
        val stP = ink(pal.sub, u * 0.075f, false)
        canvas.drawText(snap?.updatedLabel ?: "--:--", pad, h - pad * 0.6f, stP)
        WidgetTextHelper.drawRefreshGlyph(
            canvas, w - pad * 1.15f - refR, h - pad * 0.82f - refR,
            refR, pal.sub
        )
    }
}