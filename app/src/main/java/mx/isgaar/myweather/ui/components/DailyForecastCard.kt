// Location: app/.../ui/components/DailyForecastCard.kt
// Function: Displays yesterday and upcoming multi-day weather rows with rain
// probability, vector icons, and gradient thermal range bars.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.DailyForecast
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.ui.components.art.ThermalBar
import mx.isgaar.myweather.ui.components.art.VectorDropletIcon
import mx.isgaar.myweather.ui.components.art.resolveDayLabel
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography

@Composable
fun DailyForecastCard(daily: List<DailyForecast>, unit: TemperatureUnit) {
    val pal = LocalWeatherPalette.current
    val gMin = daily.minOfOrNull { it.tempMinC } ?: 0.0
    val gMax = daily.maxOfOrNull { it.tempMaxC } ?: 30.0
    val span = (gMax - gMin).coerceAtLeast(1.0)

    GlassWeatherCard {
        Text(
            text = stringResource(R.string.section_daily_title),
            style = WeatherTypography.cardTitle,
            color = pal.textSecondary
        )
        Spacer(Modifier.height(10.dp))
        daily.forEach { day ->
            val startF = ((day.tempMinC - gMin) / span).toFloat()
                .coerceIn(0f, 0.9f)
            val widthF = ((day.tempMaxC - day.tempMinC) / span).toFloat()
                .coerceIn(0.1f, 1f - startF)
            val rem = (1f - startF - widthF).coerceAtLeast(0f)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = resolveDayLabel(day),
                    style = WeatherTypography.bodyTabular,
                    fontWeight = if (day.dayIndexOffset == 0) {
                        FontWeight.SemiBold
                    } else FontWeight.Normal,
                    color = if (day.dayIndexOffset < 0) pal.textSecondary
                    else pal.textPrimary,
                    modifier = Modifier.width(76.dp)
                )
                Row(
                    modifier = Modifier.width(44.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VectorDropletIcon(pal.accentRain.copy(alpha = 0.85f))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "${day.precipitationProbMax}%",
                        style = WeatherTypography.captionTabular,
                        color = pal.textSecondary
                    )
                }
                WeatherMiniIcon(day.condition, isDay = true, size = 22.dp)
                Spacer(Modifier.width(10.dp))
                Text(
                    UnitFormatter.formatTemp(day.tempMinC, unit),
                    style = WeatherTypography.bodyTabular,
                    color = pal.textSecondary,
                    modifier = Modifier.width(34.dp)
                )
                ThermalBar(startF, widthF, rem, Modifier.weight(1f))
                Spacer(Modifier.width(10.dp))
                Text(
                    UnitFormatter.formatTemp(day.tempMaxC, unit),
                    style = WeatherTypography.bodyTabular,
                    fontWeight = FontWeight.Medium,
                    color = pal.textPrimary
                )
            }
        }
    }
}
