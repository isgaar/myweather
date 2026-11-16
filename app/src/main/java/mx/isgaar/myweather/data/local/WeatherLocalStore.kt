// Location: app/.../data/local/WeatherLocalStore.kt
// Function: Persists established weather location, saved locations list,
// unit preference, and offline snapshot cache in SharedPreferences.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.local

import android.content.Context
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import org.json.JSONArray

class WeatherLocalStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadEstablishedLocation(): SavedLocation {
        val raw = prefs.getString(KEY_ACTIVE_LOCATION, null)
            ?: return SavedLocation.ORIZABA_DEFAULT
        return try {
            SnapshotJsonDecoder.decodeLocation(org.json.JSONObject(raw))
        } catch (_: Exception) {
            SavedLocation.ORIZABA_DEFAULT
        }
    }

    fun saveEstablishedLocation(location: SavedLocation) {
        val json = SnapshotJsonEncoder.encodeLocation(location).toString()
        prefs.edit().putString(KEY_ACTIVE_LOCATION, json).apply()
        val current = loadSavedLocations().filterNot { it.id == location.id }
        val updated = (listOf(location) + current).take(8)
        val arr = JSONArray().apply {
            updated.forEach { put(SnapshotJsonEncoder.encodeLocation(it)) }
        }
        prefs.edit().putString(KEY_SAVED_LOCATIONS, arr.toString()).apply()
    }

    fun loadSavedLocations(): List<SavedLocation> {
        val raw = prefs.getString(KEY_SAVED_LOCATIONS, null)
            ?: return listOf(SavedLocation.ORIZABA_DEFAULT)
        return try {
            val arr = JSONArray(raw)
            val list = List(arr.length()) { idx ->
                SnapshotJsonDecoder.decodeLocation(arr.getJSONObject(idx))
            }
            list.ifEmpty { listOf(SavedLocation.ORIZABA_DEFAULT) }
        } catch (_: Exception) {
            listOf(SavedLocation.ORIZABA_DEFAULT)
        }
    }

    fun loadTemperatureUnit(): TemperatureUnit {
        val raw = prefs.getString(KEY_UNIT, TemperatureUnit.CELSIUS.name)
        return if (raw == TemperatureUnit.FAHRENHEIT.name) {
            TemperatureUnit.FAHRENHEIT
        } else {
            TemperatureUnit.CELSIUS
        }
    }

    fun saveTemperatureUnit(unit: TemperatureUnit) {
        prefs.edit().putString(KEY_UNIT, unit.name).apply()
    }

    fun loadCachedSnapshot(): WeatherSnapshot? {
        return SnapshotJsonCodec.decode(prefs.getString(KEY_SNAPSHOT, null))
    }

    fun saveCachedSnapshot(snapshot: WeatherSnapshot) {
        val encoded = SnapshotJsonCodec.encode(snapshot)
        prefs.edit().putString(KEY_SNAPSHOT, encoded).apply()
    }

    companion object {
        private const val PREFS_NAME = "myweather_local_store"
        private const val KEY_ACTIVE_LOCATION = "active_location"
        private const val KEY_SAVED_LOCATIONS = "saved_locations"
        private const val KEY_UNIT = "temp_unit"
        private const val KEY_SNAPSHOT = "cached_snapshot"
    }
}
