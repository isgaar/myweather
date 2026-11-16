// Location: app/.../widget/data/WidgetRemoteViewsFactory.kt
// Function: Builds Android RemoteViews with rendered Canvas bitmaps and wires
// tap-to-open and tap-to-refresh PendingIntents safely with FLAG_IMMUTABLE.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.data

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.widget.RemoteViews
import mx.isgaar.myweather.MainActivity
import mx.isgaar.myweather.R
import mx.isgaar.myweather.widget.provider.WidgetRefreshReceiver

object WidgetRemoteViewsFactory {

    private val flags = PendingIntent.FLAG_UPDATE_CURRENT or
        PendingIntent.FLAG_IMMUTABLE

    fun sizeDp(
        ctx: Context,
        mgr: AppWidgetManager,
        widgetId: Int
    ): Pair<Int, Int> {
        val opts = mgr.getAppWidgetOptions(widgetId)
        val portrait = ctx.resources.configuration.orientation ==
            Configuration.ORIENTATION_PORTRAIT
        val wKey = if (portrait) {
            AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH
        } else {
            AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
        }
        val hKey = if (portrait) {
            AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
        } else {
            AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT
        }
        val wDp = opts.getInt(wKey, 180).takeIf { it > 0 } ?: 180
        val hDp = opts.getInt(hKey, 180).takeIf { it > 0 } ?: 180
        return wDp to hDp
    }

    fun build(ctx: Context, bitmap: Bitmap, widgetId: Int): RemoteViews {
        val views = RemoteViews(ctx.packageName, R.layout.widget_canvas_host)
        views.setImageViewBitmap(R.id.widget_canvas_image, bitmap)

        val openPi = PendingIntent.getActivity(
            ctx, widgetId, Intent(ctx, MainActivity::class.java), flags
        )
        views.setOnClickPendingIntent(R.id.widget_canvas_image, openPi)

        val refreshIntent = Intent(
            ctx, WidgetRefreshReceiver::class.java
        ).apply {
            action = WidgetRefreshReceiver.ACTION_REFRESH
        }
        val refreshPi = PendingIntent.getBroadcast(
            ctx, widgetId + 10000, refreshIntent, flags
        )
        views.setOnClickPendingIntent(R.id.widget_refresh_hotspot, refreshPi)
        return views
    }
}

