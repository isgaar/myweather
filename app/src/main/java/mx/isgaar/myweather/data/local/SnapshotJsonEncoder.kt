// Location: app/.../data/local/SnapshotJsonEncoder.kt
// Function: Serializes WeatherSnapshot domain trees into compact JSON strings
// using org.json without reflection or third-party telemetry hooks.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.local

import mx.isgaar.myweather.domain.model.DailyForecast
import mx.isgaar.myweather.domain.model.HourlyPoint
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import org.json.JSONArray
import org.json.JSONObject

object SnapshotJsonEncoder {

    fun encode(s: WeatherSnapshot): String {
        val root = JSONObject()
        root.put("location", encodeLocation(s.location))
        root.put("updatedAt", s.updatedAtEpochMs)
        root.put("updatedLabel", s.updatedLabel)
        root.put("current", JSONObject().apply {
            put("t", s.current.temperatureC)
            put("app", s.current.apparentTempC)
            put("hum", s.current.humidityPct)
            put("pr", s.current.precipitationMm)
            put("cc", s.current.cloudCoverPct)
            put("sp", s.current.surfacePressureHpa)
            put("ws", s.current.windSpeedKmh)
            put("wg", s.current.windGustsKmh)
            put("wd", s.current.windDirectionDeg)
            put("day", s.current.isDay)
            put("cond", s.current.condition.name)
            put("uv", s.current.uvIndex)
            put("vis", s.current.visibilityKm)
        })
        s.airQuality?.let { aq ->
            root.put("aq", JSONObject().apply {
                put("aqi", aq.europeanAqi)
                put("pm25", aq.pm25UgM3)
            })
        }
        root.put("hourly", JSONArray().apply {
            s.hourly.forEach { put(encodeHour(it)) }
        })
        root.put("daily", JSONArray().apply {
            s.daily.forEach { put(encodeDay(it)) }
        })
        return root.toString()
    }

    fun encodeLocation(loc: SavedLocation): JSONObject = JSONObject().apply {
        put("id", loc.id)
        put("name", loc.name)
        put("region", loc.region)
        put("country", loc.country)
        put("lat", loc.latitude)
        put("lon", loc.longitude)
        put("elev", loc.elevationMeters)
        put("tz", loc.timezone)
    }

    private fun encodeHour(h: HourlyPoint): JSONObject = JSONObject().apply {
        put("iso", h.isoTime)
        put("lbl", h.hourLabel)
        put("t", h.temperatureC)
        put("pp", h.precipitationProb)
        put("pm", h.precipitationMm)
        put("cond", h.condition.name)
        put("day", h.isDay)
        put("uv", h.uvIndex)
        put("vis", h.visibilityMeters)
    }

    private fun encodeDay(d: DailyForecast): JSONObject = JSONObject().apply {
        put("iso", d.isoDate)
        put("off", d.dayIndexOffset)
        put("cond", d.condition.name)
        put("min", d.tempMinC)
        put("max", d.tempMaxC)
        put("sr", d.sunriseIso)
        put("ss", d.sunsetIso)
        put("uv", d.uvMax)
        put("ps", d.precipitationSumMm)
        put("pp", d.precipitationProbMax)
        put("ws", d.windMaxKmh)
    }
}
