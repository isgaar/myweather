// Location: app/.../widget/provider/ForecastWeatherWidgetProvider.kt
// Function: AppWidgetProvider rendering the resizable 4x2 hourly and 4x4 daily
// forecast widget with thermal bars and automatic background cache refresh.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.provider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import mx.isgaar.myweather.widget.data.WidgetUpdater
import mx.isgaar.myweather.widget.render.ForecastWidgetRenderer

class ForecastWeatherWidgetProvider : AppWidgetProvider() {

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
            ForecastWidgetRenderer.render(context, snap, unit, w, h)
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
            ForecastWidgetRenderer.render(context, snap, unit, w, h)
        }
    }
}
