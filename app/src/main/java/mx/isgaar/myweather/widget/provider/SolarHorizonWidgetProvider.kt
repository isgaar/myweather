// Location: app/.../widget/provider/SolarHorizonWidgetProvider.kt
// Function: AppWidgetProvider rendering the 2x2 solar horizon sine-curve widget
// with live sunrise/sunset times and automatic stale-cache refresh.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.provider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import mx.isgaar.myweather.widget.data.WidgetUpdater
import mx.isgaar.myweather.widget.render.SolarWidgetRenderer

class SolarHorizonWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        WidgetUpdater.updateSingleProvider(
            context,
            appWidgetManager,
            appWidgetIds
        ) { snap, _, w, h ->
            SolarWidgetRenderer.render(context, snap, w, h)
        }
        WidgetRefreshReceiver.refreshIfStale(context, goAsync())
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        WidgetUpdater.updateSingleProvider(
            context,
            appWidgetManager,
            intArrayOf(appWidgetId)
        ) { snap, _, w, h ->
            SolarWidgetRenderer.render(context, snap, w, h)
        }
    }
}
