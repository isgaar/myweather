// Location: app/.../data/local/DeviceLocationFinder.kt
// Function: Performs a single explicit-request coarse location lookup using
// Android LocationManager without background tracking or telemetry collection.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import mx.isgaar.myweather.domain.model.SavedLocation

object DeviceLocationFinder {

    fun hasCoarsePermission(context: Context): Boolean {
        val status = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return status == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun readLastKnownLocation(context: Context): SavedLocation? {
        if (!hasCoarsePermission(context)) return null
        val manager = context.applicationContext
            .getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null
        val best = listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        ).mapNotNull { provider ->
            runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
        }.maxByOrNull { it.time } ?: return null
        return toSavedLocation(best)
    }

    private fun toSavedLocation(loc: Location): SavedLocation {
        val lat = (loc.latitude * 1000.0).toInt() / 1000.0
        val lon = (loc.longitude * 1000.0).toInt() / 1000.0
        return SavedLocation(
            id = "device-$lat-$lon",
            name = "Ubicacion actual",
            region = "$lat, $lon",
            country = "",
            latitude = loc.latitude,
            longitude = loc.longitude,
            elevationMeters = loc.altitude.toInt()
        )
    }
}
