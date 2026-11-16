// Location: app/.../widget/provider/DynamicWeatherWidgetProvider.kt
// Function: AppWidgetProvider rendering the 2x2 dynamic atmospheric widget and
// scheduling stale-cache refreshes on update or resize events.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.provider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import mx.isgaar.myweather.widget.data.WidgetUpdater
import mx.isgaar.myweather.widget.render.DynamicWidgetRenderer

class DynamicWeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        WidgetUpdater.updateSingleProvider(
            context,
            appWidgetManager,
            appWidgetIds
        ) { snap, unit, w, h ->
            DynamicWidgetRenderer.render(context, snap, unit, w, h)
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
        ) { snap, unit, w, h ->
            DynamicWidgetRenderer.render(context, snap, unit, w, h)
        }
    }
}
