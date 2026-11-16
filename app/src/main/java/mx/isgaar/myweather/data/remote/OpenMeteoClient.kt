// Location: app/.../data/remote/OpenMeteoClient.kt
// Function: Executes zero-telemetry HTTPS requests against Open-Meteo APIs
// using bounded OkHttp timeouts and strict HTTP status validation.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.remote

import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.WeatherError
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class OpenMeteoClient(
    private val http: OkHttpClient = defaultClient()
) {

    fun fetchForecastJson(location: SavedLocation): String {
        val url = FORECAST_URL.toHttpUrl().newBuilder()
            .addQueryParameter("latitude", location.latitude.toString())
            .addQueryParameter("longitude", location.longitude.toString())
            .addQueryParameter("current", CURRENT_FIELDS)
            .addQueryParameter("hourly", HOURLY_FIELDS)
            .addQueryParameter("daily", DAILY_FIELDS)
            .addQueryParameter("past_days", "1")
            .addQueryParameter("forecast_days", "7")
            .addQueryParameter("timezone", "auto")
            .build()
        return executeGet(url.toString())
    }

    fun fetchAirQualityJson(location: SavedLocation): String? {
        val url = AIR_URL.toHttpUrl().newBuilder()
            .addQueryParameter("latitude", location.latitude.toString())
            .addQueryParameter("longitude", location.longitude.toString())
            .addQueryParameter("current", "european_aqi,pm2_5")
            .addQueryParameter("timezone", "auto")
            .build()
        return try {
            executeGet(url.toString())
        } catch (_: Exception) {
            null
        }
    }

    fun searchGeocodingJson(query: String, language: String): String {
        val url = GEO_URL.toHttpUrl().newBuilder()
            .addQueryParameter("name", query)
            .addQueryParameter("count", "8")
            .addQueryParameter("language", language)
            .addQueryParameter("format", "json")
            .build()
        return executeGet(url.toString())
    }

    private fun executeGet(url: String): String {
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .build()
        return try {
            http.newCall(request).execute().use { resp ->
                val body = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    throw WeatherError.HttpFailure(resp.code, body.take(120))
                }
                body
            }
        } catch (err: WeatherError) {
            throw err
        } catch (ex: IOException) {
            throw WeatherError.NetworkUnreachable(ex.message ?: "IO failure")
        }
    }

    companion object {
        private const val FORECAST_URL =
            "https://api.open-meteo.com/v1/forecast"
        private const val AIR_URL =
            "https://air-quality-api.open-meteo.com/v1/air-quality"
        private const val GEO_URL =
            "https://geocoding-api.open-meteo.com/v1/search"

        private const val CURRENT_FIELDS =
            "temperature_2m,relative_humidity_2m,apparent_temperature," +
                "is_day,precipitation,weather_code,cloud_cover," +
                "surface_pressure,wind_speed_10m,wind_gusts_10m," +
                "wind_direction_10m"
        private const val HOURLY_FIELDS =
            "temperature_2m,precipitation_probability,precipitation," +
                "weather_code,is_day,uv_index,visibility"
        private const val DAILY_FIELDS =
            "weather_code,temperature_2m_max,temperature_2m_min," +
                "sunrise,sunset,uv_index_max,precipitation_sum," +
                "precipitation_probability_max,wind_speed_10m_max"

        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }
}
