// Location: app/.../ui/screen/sections/WeatherHeroSection.kt
// Function: Renders the primary atmospheric hero block with large tabular
// temperature, condition label, city pin, thermal info, and hero vector art.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.screen.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.ui.components.WeatherHeroArt
import mx.isgaar.myweather.ui.components.art.VectorPinIcon
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography

@Composable
fun WeatherHeroSection(
    snap: WeatherSnapshot,
    unit: TemperatureUnit,
    condLabel: String,
    highStr: String,
    lowStr: String,
    onOpenLocations: () -> Unit
) {
    val pal = LocalWeatherPalette.current
    val feels = UnitFormatter.formatTemp(snap.current.apparentTempC, unit)
    val hl = stringResource(R.string.high_low_fmt, highStr, lowStr)
    val feelsLbl = stringResource(R.string.feels_like_fmt, feels)

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = UnitFormatter.formatTemp(
                    snap.current.temperatureC, unit
                ),
                style = WeatherTypography.heroTemp,
                color = pal.textPrimary
            )
            Text(
                text = condLabel,
                style = WeatherTypography.conditionTitle,
                color = pal.textPrimary
            )
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onOpenLocations)
            ) {
                Text(
                    text = snap.location.displayLabel(),
                    style = WeatherTypography.cityTitle,
                    color = pal.textPrimary
                )
                Spacer(Modifier.width(6.dp))
                VectorPinIcon(pal.textPrimary)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$hl  $feelsLbl",
                style = WeatherTypography.bodyTabular,
                color = pal.textSecondary
            )
            Text(
                text = stringResource(
                    R.string.elevation_fmt,
                    snap.location.elevationMeters
                ),
                style = WeatherTypography.captionTabular,
                color = pal.textSecondary
            )
        }
        WeatherHeroArt(snap.current.condition, snap.current.isDay)
    }
}
