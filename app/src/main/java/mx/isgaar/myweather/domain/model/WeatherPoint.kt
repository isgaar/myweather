// Location: app/.../domain/model/WeatherPoint.kt
// Function: Declares immutable forecast data models for hourly points, daily
// ranges including yesterday, and Open-Meteo air quality measurements.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain.model

data class HourlyPoint(
    val isoTime: String,
    val hourLabel: String,
    val temperatureC: Double,
    val precipitationProb: Int,
    val precipitationMm: Double,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val uvIndex: Double,
    val visibilityMeters: Double
)

data class DailyForecast(
    val isoDate: String,
    val dayIndexOffset: Int,
    val condition: WeatherCondition,
    val tempMinC: Double,
    val tempMaxC: Double,
    val sunriseIso: String,
    val sunsetIso: String,
    val uvMax: Double,
    val precipitationSumMm: Double,
    val precipitationProbMax: Int,
    val windMaxKmh: Double
)

data class AirQualitySnapshot(
    val europeanAqi: Int,
    val pm25UgM3: Double
)
