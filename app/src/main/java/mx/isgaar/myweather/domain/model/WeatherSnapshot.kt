// Location: app/.../domain/model/WeatherSnapshot.kt
// Function: Aggregates current conditions, hourly and daily forecasts, solar
// bounds, and user temperature unit preferences into one snapshot model.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain.model

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

data class CurrentConditions(
    val temperatureC: Double,
    val apparentTempC: Double,
    val humidityPct: Int,
    val precipitationMm: Double,
    val cloudCoverPct: Int,
    val surfacePressureHpa: Int,
    val windSpeedKmh: Int,
    val windGustsKmh: Int,
    val windDirectionDeg: Int,
    val isDay: Boolean,
    val condition: WeatherCondition,
    val uvIndex: Double,
    val visibilityKm: Double
)

data class WeatherSnapshot(
    val location: SavedLocation,
    val current: CurrentConditions,
    val hourly: List<HourlyPoint>,
    val daily: List<DailyForecast>,
    val airQuality: AirQualitySnapshot?,
    val updatedAtEpochMs: Long,
    val updatedLabel: String
) {
    fun todayForecast(): DailyForecast? {
        return daily.firstOrNull { it.dayIndexOffset == 0 }
            ?: daily.firstOrNull()
    }
}
