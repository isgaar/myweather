// Location: app/.../data/remote/OpenMeteoAirParser.kt
// Function: Parses Open-Meteo Air Quality JSON responses into optional domain
// AQI and PM2.5 snapshots without failing the primary weather forecast flow.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.remote

import mx.isgaar.myweather.domain.model.AirQualitySnapshot
import org.json.JSONObject

object OpenMeteoAirParser {

    fun parse(jsonText: String): AirQualitySnapshot? {
        return try {
            val root = JSONObject(jsonText)
            val current = root.optJSONObject("current") ?: return null
            if (!current.has("european_aqi")) return null
            AirQualitySnapshot(
                europeanAqi = current.optInt("european_aqi", 0),
                pm25UgM3 = current.optDouble("pm2_5", 0.0)
            )
        } catch (_: Exception) {
            null
        }
    }
}
