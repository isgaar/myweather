// Location: app/.../ui/components/HourlyCurveCard.kt
// Function: Renders the scrollable hourly weather card with natural summary,
// vector icons, connected temperature trend curve, and rain probabilities.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.HourlyPoint
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.ui.components.art.HourlyTrendCanvas
import mx.isgaar.myweather.ui.components.art.VectorDropletIcon
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography

@Composable
fun HourlyCurveCard(
    summary: String,
    hourly: List<HourlyPoint>,
    unit: TemperatureUnit
) {
    val pal = LocalWeatherPalette.current
    val items = hourly.take(16)
    val minT = items.minOfOrNull { it.temperatureC } ?: 0.0
    val maxT = items.maxOfOrNull { it.temperatureC } ?: 1.0
    val span = (maxT - minT).coerceAtLeast(1.0)
    val itemW = 60.dp

    GlassWeatherCard {
        Text(
            summary,
            style = WeatherTypography.cardTitle,
            color = pal.textPrimary
        )
        Spacer(Modifier.height(14.dp))
        Box(Modifier.horizontalScroll(rememberScrollState())) {
            Column {
                Row {
                    items.forEachIndexed { idx, pt ->
                        val lbl = if (idx == 0) {
                            stringResource(R.string.now_label)
                        } else pt.hourLabel
                        val t = UnitFormatter.formatTemp(pt.temperatureC, unit)
                        Column(
                            modifier = Modifier.width(itemW),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                lbl, style = WeatherTypography.captionTabular,
                                color = pal.textSecondary
                            )
                            Spacer(Modifier.height(6.dp))
                            WeatherMiniIcon(pt.condition, pt.isDay, 24.dp)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                t, style = WeatherTypography.bodyTabular,
                                fontWeight = FontWeight.Medium,
                                color = pal.textPrimary
                            )
                        }
                    }
                }
                HourlyTrendCanvas(items, itemW, minT, span)
                Row {
                    items.forEach { pt ->
                        val dropCol = pal.accentRain.copy(alpha = 0.85f)
                        Row(
                            Modifier.width(itemW).padding(top = 2.dp),
                            Arrangement.Center, Alignment.CenterVertically
                        ) {
                            VectorDropletIcon(dropCol)
                            Spacer(Modifier.width(3.dp))
                            Text(
                                "${pt.precipitationProb}%",
                                style = WeatherTypography.captionTabular,
                                color = pal.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
