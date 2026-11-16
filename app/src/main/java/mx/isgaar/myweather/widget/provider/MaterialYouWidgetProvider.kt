// Location: app/.../widget/provider/MaterialYouWidgetProvider.kt
// Function: AppWidgetProvider rendering the dynamic Material You weather
// widget and refreshing automatically on home screen update or resize events.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.provider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import mx.isgaar.myweather.widget.data.WidgetUpdater
import mx.isgaar.myweather.widget.material.MaterialYouWidgetRenderer

class MaterialYouWidgetProvider : AppWidgetProvider() {

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
            MaterialYouWidgetRenderer.render(context, snap, unit, w, h)
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
            MaterialYouWidgetRenderer.render(context, snap, unit, w, h)
        }
    }
}
