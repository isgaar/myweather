// Location: app/.../data/remote/WeatherRepositoryImpl.kt
// Function: Implements WeatherRepository by orchestrating Open-Meteo HTTPS
// calls on IO dispatchers and synchronizing local offline snapshot caching.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.isgaar.myweather.data.local.WeatherLocalStore
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.WeatherRepository
import mx.isgaar.myweather.domain.port.WeatherValidator
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WeatherRepositoryImpl(
    private val client: OpenMeteoClient,
    private val localStore: WeatherLocalStore
) : WeatherRepository {

    override suspend fun fetchWeather(
        location: SavedLocation
    ): Result<WeatherSnapshot> = withContext(Dispatchers.IO) {
        runCatching {
            WeatherValidator.requireValidLocation(location)
            val forecastRaw = client.fetchForecastJson(location)
            val airRaw = client.fetchAirQualityJson(location)
            val air = airRaw?.let { OpenMeteoAirParser.parse(it) }
            val nowLabel = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"))
            val snapshot = OpenMeteoForecastParser.parse(
                jsonText = forecastRaw,
                location = location,
                airQuality = air,
                nowEpochMs = System.currentTimeMillis(),
                updatedLabel = nowLabel
            )
            localStore.saveCachedSnapshot(snapshot)
            snapshot
        }
    }

    override suspend fun searchLocations(
        query: String,
        languageCode: String
    ): Result<List<SavedLocation>> = withContext(Dispatchers.IO) {
        runCatching {
            val clean = WeatherValidator.sanitizeSearchQuery(query)
            val raw = client.searchGeocodingJson(clean, languageCode)
            OpenMeteoGeocodingParser.parse(raw)
        }
    }

    override fun loadCachedSnapshot(): WeatherSnapshot? {
        return localStore.loadCachedSnapshot()
    }

    override fun saveCachedSnapshot(snapshot: WeatherSnapshot) {
        localStore.saveCachedSnapshot(snapshot)
    }
}
