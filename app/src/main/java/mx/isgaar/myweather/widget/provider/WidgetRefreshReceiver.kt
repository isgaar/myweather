// Location: app/.../widget/provider/WidgetRefreshReceiver.kt
// Function: Handles widget refresh broadcasts and stale-cache updates async
// using goAsync and Open-Meteo HTTPS lookups without telemetry.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.provider

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.isgaar.myweather.data.local.WeatherLocalStore
import mx.isgaar.myweather.data.remote.OpenMeteoClient
import mx.isgaar.myweather.data.remote.WeatherRepositoryImpl
import mx.isgaar.myweather.widget.data.WidgetUpdater

class WidgetRefreshReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_REFRESH) return
        val pending = goAsync()
        val appCtx = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                refreshNow(appCtx)
            } finally {
                pending.finish()
            }
        }
    }

    companion object {
        const val ACTION_REFRESH = "mx.isgaar.myweather.ACTION_REFRESH_WIDGETS"
        private const val STALE_MS = 15L * 60L * 1000L

        suspend fun refreshNow(appCtx: Context) {
            val store = WeatherLocalStore(appCtx)
            val repo = WeatherRepositoryImpl(OpenMeteoClient(), store)
            repo.fetchWeather(store.loadEstablishedLocation())
            WidgetUpdater.updateAll(appCtx)
        }

        fun refreshIfStale(context: Context, pending: PendingResult) {
            val appCtx = context.applicationContext
            val store = WeatherLocalStore(appCtx)
            val cached = store.loadCachedSnapshot()
            val age = System.currentTimeMillis() -
                (cached?.updatedAtEpochMs ?: 0L)
            if (cached != null && age < STALE_MS) {
                pending.finish()
                return
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    refreshNow(appCtx)
                } finally {
                    pending.finish()
                }
            }
        }
    }
}
