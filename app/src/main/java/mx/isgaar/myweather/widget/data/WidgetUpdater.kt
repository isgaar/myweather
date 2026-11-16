// Location: app/.../widget/data/WidgetUpdater.kt
// Function: Coordinates AppWidgetManager updates across all four home screen
// weather widgets and delegates RemoteViews creation to the factory helper.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.data

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import mx.isgaar.myweather.data.local.WeatherLocalStore
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.widget.provider.DynamicWeatherWidgetProvider
import mx.isgaar.myweather.widget.provider.ForecastWeatherWidgetProvider
import mx.isgaar.myweather.widget.provider.MinimalWeatherWidgetProvider
import mx.isgaar.myweather.widget.provider.SolarHorizonWidgetProvider
import mx.isgaar.myweather.widget.render.DynamicWidgetRenderer
import mx.isgaar.myweather.widget.render.ForecastWidgetRenderer
import mx.isgaar.myweather.widget.render.MinimalWidgetRenderer
import mx.isgaar.myweather.widget.render.SolarWidgetRenderer

object WidgetUpdater {

    fun updateAll(context: Context) {
        val appCtx = context.applicationContext
        val store = WeatherLocalStore(appCtx)
        val snap = store.loadCachedSnapshot()
        val unit = store.loadTemperatureUnit()
        val mgr = AppWidgetManager.getInstance(appCtx)

        updateProvider(
            appCtx, mgr, DynamicWeatherWidgetProvider::class.java
        ) { w, h -> DynamicWidgetRenderer.render(appCtx, snap, unit, w, h) }
        updateProvider(
            appCtx, mgr, MinimalWeatherWidgetProvider::class.java
        ) { w, h -> MinimalWidgetRenderer.render(appCtx, snap, unit, w, h) }
        updateProvider(
            appCtx, mgr, SolarHorizonWidgetProvider::class.java
        ) { w, h -> SolarWidgetRenderer.render(appCtx, snap, w, h) }
        updateProvider(
            appCtx, mgr, ForecastWeatherWidgetProvider::class.java
        ) { w, h -> ForecastWidgetRenderer.render(appCtx, snap, unit, w, h) }
    }

    fun updateSingleProvider(
        context: Context,
        mgr: AppWidgetManager,
        appWidgetIds: IntArray,
        render: (WeatherSnapshot?, TemperatureUnit, Int, Int) -> Bitmap
    ) {
        val appCtx = context.applicationContext
        val store = WeatherLocalStore(appCtx)
        val snap = store.loadCachedSnapshot()
        val unit = store.loadTemperatureUnit()
        for (id in appWidgetIds) {
            val (wDp, hDp) = WidgetRemoteViewsFactory.sizeDp(appCtx, mgr, id)
            val bmp = render(snap, unit, wDp, hDp)
            mgr.updateAppWidget(
                id, WidgetRemoteViewsFactory.build(appCtx, bmp, id)
            )
        }
    }

    private fun updateProvider(
        ctx: Context,
        mgr: AppWidgetManager,
        cls: Class<*>,
        render: (Int, Int) -> Bitmap
    ) {
        val ids = mgr.getAppWidgetIds(ComponentName(ctx, cls))
        for (id in ids) {
            val (wDp, hDp) = WidgetRemoteViewsFactory.sizeDp(ctx, mgr, id)
            val views = WidgetRemoteViewsFactory.build(
                ctx, render(wDp, hDp), id
            )
            mgr.updateAppWidget(id, views)
        }
    }
}
