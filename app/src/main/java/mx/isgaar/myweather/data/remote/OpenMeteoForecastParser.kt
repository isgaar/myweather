// Location: app/.../data/remote/OpenMeteoForecastParser.kt
// Function: Parses Open-Meteo forecast JSON payloads at the network boundary
// into validated WeatherSnapshot domain models via OpenMeteoSeriesParser.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.remote

import mx.isgaar.myweather.domain.model.AirQualitySnapshot
import mx.isgaar.myweather.domain.model.CurrentConditions
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherError
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import org.json.JSONObject
import kotlin.math.roundToInt

object OpenMeteoForecastParser {

    fun parse(
        jsonText: String,
        location: SavedLocation,
        airQuality: AirQualitySnapshot?,
        nowEpochMs: Long,
        updatedLabel: String
    ): WeatherSnapshot {
        val root = try {
            JSONObject(jsonText)
        } catch (_: Exception) {
            throw WeatherError.InvalidPayload("Invalid JSON root")
        }
        val cur = root.optJSONObject("current")
            ?: throw WeatherError.InvalidPayload("Missing current block")
        val nowIso = cur.optString("time")
        val hourly = OpenMeteoSeriesParser.parseHourly(
            root.optJSONObject("hourly"),
            nowIso
        )
        val daily = OpenMeteoSeriesParser.parseDaily(
            root.optJSONObject("daily")
        )
        val elev = root.optDouble(
            "elevation",
            location.elevationMeters.toDouble()
        )
        val firstHour = hourly.firstOrNull()
        val temp = cur.getDouble("temperature_2m")
        val wCode = cur.optInt("weather_code", 0)
        val current = CurrentConditions(
            temperatureC = temp,
            apparentTempC = cur.optDouble("apparent_temperature", temp),
            humidityPct = cur.optInt("relative_humidity_2m", 0),
            precipitationMm = cur.optDouble("precipitation", 0.0),
            cloudCoverPct = cur.optInt("cloud_cover", 0),
            surfacePressureHpa = cur.optDouble("surface_pressure", 1013.0)
                .roundToInt(),
            windSpeedKmh = cur.optDouble("wind_speed_10m", 0.0).roundToInt(),
            windGustsKmh = cur.optDouble("wind_gusts_10m", 0.0).roundToInt(),
            windDirectionDeg = cur.optInt("wind_direction_10m", 0),
            isDay = cur.optInt("is_day", 1) == 1,
            condition = WeatherCondition.fromWmoCode(wCode),
            uvIndex = firstHour?.uvIndex ?: 0.0,
            visibilityKm = (firstHour?.visibilityMeters ?: 10000.0) / 1000.0
        )
        return WeatherSnapshot(
            location = location.copy(elevationMeters = elev.roundToInt()),
            current = current,
            hourly = hourly,
            daily = daily,
            airQuality = airQuality,
            updatedAtEpochMs = nowEpochMs,
            updatedLabel = updatedLabel
        )
    }
}
