// Location: app/.../data/remote/OpenMeteoSeriesParser.kt
// Function: Extracts hourly and daily forecast sequences from Open-Meteo JSON
// arrays while preserving yesterday offset -1 and next 24-hour windows.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.remote

import mx.isgaar.myweather.domain.model.DailyForecast
import mx.isgaar.myweather.domain.model.HourlyPoint
import mx.isgaar.myweather.domain.model.WeatherCondition
import org.json.JSONObject

object OpenMeteoSeriesParser {

    fun parseHourly(obj: JSONObject?, currentIso: String): List<HourlyPoint> {
        if (obj == null) return emptyList()
        val times = obj.optJSONArray("time") ?: return emptyList()
        val temps = obj.optJSONArray("temperature_2m") ?: return emptyList()
        val probs = obj.optJSONArray("precipitation_probability")
        val precips = obj.optJSONArray("precipitation")
        val codes = obj.optJSONArray("weather_code")
        val isDays = obj.optJSONArray("is_day")
        val uvs = obj.optJSONArray("uv_index")
        val vis = obj.optJSONArray("visibility")
        val prefix = currentIso.take(13)
        var startIdx = 0
        for (i in 0 until times.length()) {
            if (times.optString(i).startsWith(prefix)) {
                startIdx = i
                break
            }
        }
        val endIdx = minOf(times.length(), startIdx + 24)
        val out = ArrayList<HourlyPoint>(endIdx - startIdx)
        for (i in startIdx until endIdx) {
            val iso = times.optString(i)
            val code = codes?.optInt(i, 0) ?: 0
            out.add(
                HourlyPoint(
                    isoTime = iso,
                    hourLabel = iso.substringAfter('T', "00:00").take(5),
                    temperatureC = temps.optDouble(i, 0.0),
                    precipitationProb = probs?.optInt(i, 0) ?: 0,
                    precipitationMm = precips?.optDouble(i, 0.0) ?: 0.0,
                    condition = WeatherCondition.fromWmoCode(code),
                    isDay = (isDays?.optInt(i, 1) ?: 1) == 1,
                    uvIndex = uvs?.optDouble(i, 0.0) ?: 0.0,
                    visibilityMeters = vis?.optDouble(i, 10000.0) ?: 10000.0
                )
            )
        }
        return out
    }

    fun parseDaily(obj: JSONObject?): List<DailyForecast> {
        if (obj == null) return emptyList()
        val dates = obj.optJSONArray("time") ?: return emptyList()
        val codes = obj.optJSONArray("weather_code")
        val maxT = obj.optJSONArray("temperature_2m_max")
        val minT = obj.optJSONArray("temperature_2m_min")
        val rises = obj.optJSONArray("sunrise")
        val sets = obj.optJSONArray("sunset")
        val uvs = obj.optJSONArray("uv_index_max")
        val pSum = obj.optJSONArray("precipitation_sum")
        val pProb = obj.optJSONArray("precipitation_probability_max")
        val winds = obj.optJSONArray("wind_speed_10m_max")
        return List(dates.length()) { i ->
            val code = codes?.optInt(i, 0) ?: 0
            DailyForecast(
                isoDate = dates.optString(i),
                dayIndexOffset = i - 1,
                condition = WeatherCondition.fromWmoCode(code),
                tempMinC = minT?.optDouble(i, 0.0) ?: 0.0,
                tempMaxC = maxT?.optDouble(i, 0.0) ?: 0.0,
                sunriseIso = rises?.optString(i).orEmpty(),
                sunsetIso = sets?.optString(i).orEmpty(),
                uvMax = uvs?.optDouble(i, 0.0) ?: 0.0,
                precipitationSumMm = pSum?.optDouble(i, 0.0) ?: 0.0,
                precipitationProbMax = pProb?.optInt(i, 0) ?: 0,
                windMaxKmh = winds?.optDouble(i, 0.0) ?: 0.0
            )
        }
    }
}
