// Location: app/.../domain/port/WeatherValidator.kt
// Function: Boundary validation helpers checking coordinate ranges and city
// search queries before issuing outbound HTTPS requests.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain.port

import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.WeatherError

object WeatherValidator {

    fun requireValidLocation(location: SavedLocation) {
        val validLat = location.latitude in -90.0..90.0 &&
            !location.latitude.isNaN()
        val validLon = location.longitude in -180.0..180.0 &&
            !location.longitude.isNaN()
        if (!validLat || !validLon) {
            throw WeatherError.InvalidLocation("Invalid coordinates")
        }
    }

    fun sanitizeSearchQuery(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.length < 2) {
            throw WeatherError.InvalidLocation("Query too short")
        }
        return trimmed.take(64)
    }
}
