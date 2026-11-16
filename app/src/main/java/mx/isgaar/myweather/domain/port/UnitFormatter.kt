// Location: app/.../domain/port/UnitFormatter.kt
// Function: Pure formatting utilities for Celsius and Fahrenheit temperatures,
// wind cardinal bearings, UV severity labels, and air quality tiers.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain.port

import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import kotlin.math.roundToInt

object UnitFormatter {

    fun convertTemp(celsius: Double, unit: TemperatureUnit): Double {
        return when (unit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> (celsius * 9.0 / 5.0) + 32.0
        }
    }

    fun formatTemp(celsius: Double, unit: TemperatureUnit): String {
        val rounded = convertTemp(celsius, unit).roundToInt()
        return "$rounded\u00B0"
    }

    fun cardinalDirection(degrees: Int): String {
        val dirs = listOf("N", "NE", "E", "SE", "S", "SO", "O", "NO")
        val normalized = ((degrees % 360) + 360) % 360
        val index = ((normalized + 22) / 45) % 8
        return dirs[index]
    }

    fun uvLabelRes(uv: Double): Int = when {
        uv < 3.0 -> R.string.uv_low
        uv < 6.0 -> R.string.uv_moderate
        uv < 8.0 -> R.string.uv_high
        else -> R.string.uv_very_high
    }

    fun aqiLabelRes(aqi: Int): Int = when {
        aqi <= 20 -> R.string.aqi_good
        aqi <= 40 -> R.string.aqi_fair
        aqi <= 60 -> R.string.aqi_moderate
        else -> R.string.aqi_poor
    }
}
