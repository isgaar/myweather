// Location: app/.../ui/components/WeatherHeroArt.kt
// Function: Renders custom vector Canvas celestial and meteorological hero art
// inspired by the reference design without bitmap or emoji assets.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.ui.components.art.HeroCelestialDraw
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette

@Composable
fun WeatherHeroArt(
    condition: WeatherCondition,
    isDay: Boolean,
    modifier: Modifier = Modifier
) {
    val pal = LocalWeatherPalette.current
    Canvas(modifier = modifier.size(132.dp)) {
        val w = size.width
        val h = size.height
        val showCloud = condition != WeatherCondition.CLEAR &&
            condition != WeatherCondition.MOSTLY_CLEAR
        val c = if (showCloud) Offset(w * 0.63f, h * 0.35f)
        else Offset(w * 0.55f, h * 0.46f)
        val r = if (showCloud) w * 0.22f else w * 0.31f
        if (isDay) {
            HeroCelestialDraw.drawSun(
                this, c, r, pal.accentSun, pal.accentSunWarm
            )
        } else {
            HeroCelestialDraw.drawCrescent(
                this, c, r, pal.accentMoon, pal.accentSunWarm,
                pal.skyTop, pal.textPrimary
            )
        }
        if (showCloud) {
            HeroCelestialDraw.drawCloud(
                this, w, h, pal.textPrimary.copy(alpha = 0.94f)
            )
        }
        HeroCelestialDraw.drawPrecip(
            this, condition, w, h, pal.accentRain, pal.accentSun
        )
    }
}
