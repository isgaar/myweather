// Location: app/.../data/remote/OpenMeteoGeocodingParser.kt
// Function: Parses Open-Meteo geocoding search JSON payloads into validated
// SavedLocation candidates for establishing the user weather city.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.remote

import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.WeatherError
import org.json.JSONObject
import kotlin.math.roundToInt

object OpenMeteoGeocodingParser {

    fun parse(jsonText: String): List<SavedLocation> {
        val root = try {
            JSONObject(jsonText)
        } catch (_: Exception) {
            throw WeatherError.InvalidPayload("Invalid geocoding JSON")
        }
        val results = root.optJSONArray("results") ?: return emptyList()
        val list = ArrayList<SavedLocation>(results.length())
        for (i in 0 until results.length()) {
            val item = results.optJSONObject(i) ?: continue
            val lat = item.optDouble("latitude", Double.NaN)
            val lon = item.optDouble("longitude", Double.NaN)
            val name = item.optString("name").trim()
            if (name.isEmpty() || lat.isNaN() || lon.isNaN()) continue
            list.add(
                SavedLocation(
                    id = item.optLong("id", i.toLong()).toString(),
                    name = name,
                    region = item.optString("admin1").trim(),
                    country = item.optString("country").trim(),
                    latitude = lat,
                    longitude = lon,
                    elevationMeters = item.optDouble("elevation", 0.0)
                        .roundToInt(),
                    timezone = item.optString("timezone", "auto")
                )
            )
        }
        return list
    }
}
