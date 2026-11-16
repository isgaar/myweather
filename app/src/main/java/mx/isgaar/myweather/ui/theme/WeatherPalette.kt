// Location: app/.../ui/theme/WeatherPalette.kt
// Function: Resolves dynamic atmospheric sky gradients and glass surface tokens
// strictly from colors.xml resources without hardcoded Compose hex literals.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.WeatherCondition

@Immutable
data class WeatherPalette(
    val skyTop: Color,
    val skyBottom: Color,
    val glassSurface: Color,
    val glassStrong: Color,
    val glassBorder: Color,
    val sheetSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val accentSun: Color,
    val accentSunWarm: Color,
    val accentMoon: Color,
    val accentRain: Color,
    val accentCurve: Color,
    val accentCool: Color,
    val accentWarm: Color,
    val accentHot: Color
)

val LocalWeatherPalette = staticCompositionLocalOf<WeatherPalette> {
    error("WeatherPalette not provided")
}

@Composable
fun rememberAtmosphericPalette(
    condition: WeatherCondition,
    isDay: Boolean
): WeatherPalette {
    val isRain = condition in listOf(
        WeatherCondition.DRIZZLE,
        WeatherCondition.RAIN_LIGHT,
        WeatherCondition.RAIN_MODERATE,
        WeatherCondition.RAIN_HEAVY,
        WeatherCondition.STORM
    )
    val isCloudy = condition in listOf(
        WeatherCondition.OVERCAST,
        WeatherCondition.FOG
    )
    val topColor = when {
        !isDay -> colorResource(R.color.sky_night_top)
        isRain -> colorResource(R.color.sky_rain_top)
        isCloudy -> colorResource(R.color.sky_overcast_top)
        else -> colorResource(R.color.sky_day_top)
    }
    val bottomColor = when {
        !isDay -> colorResource(R.color.sky_night_bottom)
        isRain -> colorResource(R.color.sky_rain_bottom)
        isCloudy -> colorResource(R.color.sky_overcast_bottom)
        else -> colorResource(R.color.sky_day_bottom)
    }
    return WeatherPalette(
        skyTop = topColor,
        skyBottom = bottomColor,
        glassSurface = colorResource(R.color.surface_glass),
        glassStrong = colorResource(R.color.surface_glass_strong),
        glassBorder = colorResource(R.color.border_glass),
        sheetSurface = colorResource(R.color.surface_sheet),
        textPrimary = colorResource(R.color.text_primary),
        textSecondary = colorResource(R.color.text_secondary),
        textMuted = colorResource(R.color.text_muted),
        accentSun = colorResource(R.color.accent_sun),
        accentSunWarm = colorResource(R.color.accent_sun_warm),
        accentMoon = colorResource(R.color.accent_moon),
        accentRain = colorResource(R.color.accent_rain),
        accentCurve = colorResource(R.color.accent_curve),
        accentCool = colorResource(R.color.accent_cool),
        accentWarm = colorResource(R.color.accent_warm),
        accentHot = colorResource(R.color.accent_hot)
    )
}
