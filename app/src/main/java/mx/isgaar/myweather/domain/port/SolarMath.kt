// Location: app/.../domain/port/SolarMath.kt
// Function: Pure solar cycle calculations extracting ISO hour-minute values
// and computing normalized horizon arc progress between sunrise and sunset.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain.port

object SolarMath {

    fun parseIsoMinutes(isoDateTime: String): Int? {
        val tIndex = isoDateTime.indexOf('T')
        if (tIndex < 0 || tIndex + 5 >= isoDateTime.length) return null
        val timePart = isoDateTime.substring(tIndex + 1, tIndex + 6)
        val parts = timePart.split(':')
        if (parts.size != 2) return null
        val hour = parts[0].toIntOrNull() ?: return null
        val min = parts[1].toIntOrNull() ?: return null
        if (hour !in 0..23 || min !in 0..59) return null
        return hour * 60 + min
    }

    fun formatIsoClock(isoDateTime: String): String {
        val minutes = parseIsoMinutes(isoDateTime) ?: return "--:--"
        val hour24 = minutes / 60
        val min = minutes % 60
        val period = if (hour24 < 12) "AM" else "PM"
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        return "$hour12:${min.toString().padStart(2, '0')} $period"
    }

    fun dayProgress(
        nowMinutes: Int,
        sunriseMinutes: Int,
        sunsetMinutes: Int
    ): Float {
        if (sunsetMinutes <= sunriseMinutes) return 0.5f
        if (nowMinutes <= sunriseMinutes) return 0f
        if (nowMinutes >= sunsetMinutes) return 1f
        val span = (sunsetMinutes - sunriseMinutes).toFloat()
        return ((nowMinutes - sunriseMinutes) / span).coerceIn(0f, 1f)
    }
}
