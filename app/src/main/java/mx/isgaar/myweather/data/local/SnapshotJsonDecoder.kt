// Location: app/.../data/local/SnapshotJsonDecoder.kt
// Function: Reconstructs WeatherSnapshot domain graphs from locally cached
// JSON strings with defensive fallbacks on missing or unknown enum fields.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.local

import mx.isgaar.myweather.domain.model.AirQualitySnapshot
import mx.isgaar.myweather.domain.model.CurrentConditions
import mx.isgaar.myweather.domain.model.DailyForecast
import mx.isgaar.myweather.domain.model.HourlyPoint
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import org.json.JSONArray
import org.json.JSONObject

object SnapshotJsonDecoder {

    fun decode(raw: String): WeatherSnapshot {
        val root = JSONObject(raw)
        val loc = decodeLocation(root.getJSONObject("location"))
        val c = root.getJSONObject("current")
        val current = CurrentConditions(
            temperatureC = c.getDouble("t"),
            apparentTempC = c.getDouble("app"),
            humidityPct = c.getInt("hum"),
            precipitationMm = c.getDouble("pr"),
            cloudCoverPct = c.getInt("cc"),
            surfacePressureHpa = c.getInt("sp"),
            windSpeedKmh = c.getInt("ws"),
            windGustsKmh = c.getInt("wg"),
            windDirectionDeg = c.getInt("wd"),
            isDay = c.getBoolean("day"),
            condition = parseCondition(c.getString("cond")),
            uvIndex = c.optDouble("uv", 0.0),
            visibilityKm = c.optDouble("vis", 10.0)
        )
        val aqObj = root.optJSONObject("aq")
        val aq = aqObj?.let {
            AirQualitySnapshot(it.getInt("aqi"), it.getDouble("pm25"))
        }
        return WeatherSnapshot(
            location = loc,
            current = current,
            hourly = decodeHours(root.optJSONArray("hourly")),
            daily = decodeDays(root.optJSONArray("daily")),
            airQuality = aq,
            updatedAtEpochMs = root.getLong("updatedAt"),
            updatedLabel = root.optString("updatedLabel", "--:--")
        )
    }

    fun decodeLocation(obj: JSONObject): SavedLocation = SavedLocation(
        id = obj.getString("id"),
        name = obj.getString("name"),
        region = obj.optString("region"),
        country = obj.optString("country"),
        latitude = obj.getDouble("lat"),
        longitude = obj.getDouble("lon"),
        elevationMeters = obj.optInt("elev", 0),
        timezone = obj.optString("tz", "America/Mexico_City")
    )

    private fun decodeHours(arr: JSONArray?): List<HourlyPoint> {
        if (arr == null) return emptyList()
        return List(arr.length()) { idx ->
            val o = arr.getJSONObject(idx)
            HourlyPoint(
                isoTime = o.getString("iso"),
                hourLabel = o.getString("lbl"),
                temperatureC = o.getDouble("t"),
                precipitationProb = o.getInt("pp"),
                precipitationMm = o.getDouble("pm"),
                condition = parseCondition(o.getString("cond")),
                isDay = o.getBoolean("day"),
                uvIndex = o.optDouble("uv", 0.0),
                visibilityMeters = o.optDouble("vis", 10000.0)
            )
        }
    }

    private fun decodeDays(arr: JSONArray?): List<DailyForecast> {
        if (arr == null) return emptyList()
        return List(arr.length()) { idx ->
            val o = arr.getJSONObject(idx)
            DailyForecast(
                isoDate = o.getString("iso"),
                dayIndexOffset = o.getInt("off"),
                condition = parseCondition(o.getString("cond")),
                tempMinC = o.getDouble("min"),
                tempMaxC = o.getDouble("max"),
                sunriseIso = o.optString("sr"),
                sunsetIso = o.optString("ss"),
                uvMax = o.optDouble("uv", 0.0),
                precipitationSumMm = o.optDouble("ps", 0.0),
                precipitationProbMax = o.optInt("pp", 0),
                windMaxKmh = o.optDouble("ws", 0.0)
            )
        }
    }

    private fun parseCondition(name: String): WeatherCondition {
        return WeatherCondition.entries.firstOrNull { it.name == name }
            ?: WeatherCondition.PARTLY_CLOUDY
    }
}
