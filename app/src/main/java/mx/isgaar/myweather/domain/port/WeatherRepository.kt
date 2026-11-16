// Location: app/.../domain/port/WeatherRepository.kt
// Function: Port interface decoupling domain and UI layers from remote
// Open-Meteo HTTPS transport and local established-location persistence.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain.port

import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.WeatherSnapshot

interface WeatherRepository {
    suspend fun fetchWeather(location: SavedLocation): Result<WeatherSnapshot>
    suspend fun searchLocations(
        query: String,
        languageCode: String
    ): Result<List<SavedLocation>>
    fun loadCachedSnapshot(): WeatherSnapshot?
    fun saveCachedSnapshot(snapshot: WeatherSnapshot)
}
