// Location: app/.../domain/model/WeatherCondition.kt
// Function: Enumerates WMO meteorological conditions and maps numeric WMO
// codes to localized string resource identifiers and visual themes.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain.model

import mx.isgaar.myweather.R

enum class WeatherCondition(val labelRes: Int) {
    CLEAR(R.string.cond_clear),
    MOSTLY_CLEAR(R.string.cond_mostly_clear),
    PARTLY_CLOUDY(R.string.cond_partly_cloudy),
    OVERCAST(R.string.cond_overcast),
    FOG(R.string.cond_fog),
    DRIZZLE(R.string.cond_drizzle),
    RAIN_LIGHT(R.string.cond_rain_light),
    RAIN_MODERATE(R.string.cond_rain_moderate),
    RAIN_HEAVY(R.string.cond_rain_heavy),
    STORM(R.string.cond_storm),
    SNOW(R.string.cond_snow);

    companion object {
        fun fromWmoCode(code: Int): WeatherCondition = when (code) {
            0 -> CLEAR
            1 -> MOSTLY_CLEAR
            2 -> PARTLY_CLOUDY
            3 -> OVERCAST
            45, 48 -> FOG
            51, 53, 55, 56, 57 -> DRIZZLE
            61, 66, 80 -> RAIN_LIGHT
            63, 81 -> RAIN_MODERATE
            65, 67, 82 -> RAIN_HEAVY
            71, 73, 75, 77, 85, 86 -> SNOW
            95, 96, 99 -> STORM
            else -> PARTLY_CLOUDY
        }
    }
}
