// Location: app/.../MyWeatherApp.kt
// Function: Application entry class enforcing zero-telemetry initialization and
// ComponentCallbacks2 memory-trim hooks without Activity context retention.
// All Rights Reserved isgaar

package mx.isgaar.myweather

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration

class MyWeatherApp : Application() {

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            System.gc()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}
