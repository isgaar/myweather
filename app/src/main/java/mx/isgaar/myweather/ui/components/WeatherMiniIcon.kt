// Location: app/.../ui/components/WeatherMiniIcon.kt
// Function: Draws compact vector Canvas weather condition icons for hourly
// strips and daily forecast rows without bitmap decoding overhead.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette

@Composable
fun WeatherMiniIcon(
    condition: WeatherCondition,
    isDay: Boolean,
    size: Dp = 24.dp
) {
    val palette = LocalWeatherPalette.current
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val hasCloud = condition != WeatherCondition.CLEAR &&
            condition != WeatherCondition.MOSTLY_CLEAR
        val showCelestial = condition == WeatherCondition.CLEAR ||
            condition == WeatherCondition.MOSTLY_CLEAR ||
            condition == WeatherCondition.PARTLY_CLOUDY
        if (showCelestial) {
            val c = if (hasCloud) Offset(w * 0.64f, h * 0.34f)
            else Offset(w * 0.5f, h * 0.5f)
            val r = if (hasCloud) w * 0.22f else w * 0.32f
            drawCircle(
                color = if (isDay) palette.accentSun else palette.accentMoon,
                radius = r,
                center = c
            )
            if (!isDay) {
                drawCircle(
                    color = palette.skyTop,
                    radius = r * 0.8f,
                    center = Offset(c.x - r * 0.36f, c.y - r * 0.25f)
                )
            }
        }
        if (hasCloud) {
            val cy = h * 0.58f
            val cloud = palette.textPrimary
            drawCircle(cloud, w * 0.18f, Offset(w * 0.36f, cy - h * 0.08f))
            drawCircle(cloud, w * 0.22f, Offset(w * 0.54f, cy - h * 0.12f))
            drawRoundRect(
                color = cloud,
                topLeft = Offset(w * 0.20f, cy - h * 0.06f),
                size = Size(w * 0.60f, h * 0.24f)
            )
        }
        val rain = condition in listOf(
            WeatherCondition.DRIZZLE,
            WeatherCondition.RAIN_LIGHT,
            WeatherCondition.RAIN_MODERATE,
            WeatherCondition.RAIN_HEAVY,
            WeatherCondition.STORM
        )
        if (rain) {
            listOf(0.36f, 0.52f, 0.68f).forEach { rx ->
                drawLine(
                    color = palette.accentRain,
                    start = Offset(w * rx, h * 0.80f),
                    end = Offset(w * (rx - 0.06f), h * 0.94f),
                    strokeWidth = 2.4f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
