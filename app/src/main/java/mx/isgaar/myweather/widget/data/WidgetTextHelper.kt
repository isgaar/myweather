// Location: app/.../widget/data/WidgetTextHelper.kt
// Function: Shared Canvas text auto-fit, ellipsis truncation, location arrow,
// and refined arrowhead refresh glyph helpers for home screen widgets.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.data

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

object WidgetTextHelper {

    fun fit(
        paint: Paint,
        text: String,
        maxW: Float,
        minPx: Float = 12f
    ): Paint {
        val target = maxW.coerceAtLeast(24f)
        while (paint.textSize > minPx && paint.measureText(text) > target) {
            paint.textSize = (paint.textSize * 0.92f).coerceAtLeast(minPx)
        }
        return paint
    }

    fun ellipsize(paint: Paint, text: String, maxW: Float): String {
        if (paint.measureText(text) <= maxW) return text
        var end = text.length - 1
        while (end > 1 && paint.measureText(text.take(end) + "...") > maxW) {
            end--
        }
        return text.take(end.coerceAtLeast(1)) + "..."
    }

    fun drawLocationArrow(
        canvas: Canvas, cx: Float, cy: Float, sz: Float, c: Int
    ) {
        val p = Path().apply {
            moveTo(cx + sz * 0.45f, cy - sz * 0.45f)
            lineTo(cx - sz * 0.45f, cy + sz * 0.05f)
            lineTo(cx - sz * 0.02f, cy + sz * 0.08f)
            lineTo(cx + sz * 0.06f, cy + sz * 0.48f)
            close()
        }
        canvas.drawPath(p, WidgetDrawPrimitives.paint(c))
    }

    fun drawRefreshGlyph(
        canvas: Canvas, cx: Float, cy: Float, r: Float, col: Int
    ) {
        val sw = (r * 0.24f).coerceIn(2f, 4.5f)
        val p = WidgetDrawPrimitives.paint(col).apply {
            style = Paint.Style.STROKE
            strokeWidth = sw
            strokeCap = Paint.Cap.ROUND
            alpha = 205
        }
        val box = RectF(cx - r, cy - r, cx + r, cy + r)
        canvas.drawArc(box, -35f, 280f, false, p)
        val tipX = cx + r * 0.82f
        val tipY = cy - r * 0.54f
        val head = Path().apply {
            moveTo(tipX, tipY)
            lineTo(tipX - r * 0.42f, tipY - r * 0.08f)
            lineTo(tipX + r * 0.06f, tipY - r * 0.46f)
            close()
        }
        val hp = WidgetDrawPrimitives.paint(col).apply { alpha = 205 }
        canvas.drawPath(head, hp)
    }

    fun drawHorizonIcon(
        canvas: Canvas, x: Float, y: Float, r: Float, col: Int
    ) {
        val p = WidgetDrawPrimitives.paint(col).apply { strokeWidth = 2f }
        canvas.drawCircle(x + r, y - r * 0.2f, r * 0.72f, p)
        canvas.drawLine(x - 1f, y + r * 0.55f, x + r * 2.1f, y + r * 0.55f, p)
    }

    fun drawSplitClock(
        canvas: Canvas, clk: String, x: Float, y: Float, maxW: Float,
        tp: (Float) -> Paint, baseSz: Float
    ) {
        val parts = clk.split(" ")
        val time = parts.firstOrNull() ?: clk
        val amp = parts.getOrNull(1).orEmpty()
        var sz = baseSz
        while (sz > 13f) {
            val wTot = tp(sz).measureText(time) +
                tp(sz * 0.66f).measureText(amp)
            if (wTot <= maxW) break else sz *= 0.92f
        }
        val mp = tp(sz)
        canvas.drawText(time, x, y, mp)
        canvas.drawText(amp, x + mp.measureText(time) + 2f, y, tp(sz * 0.66f))
    }
}

