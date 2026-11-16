// Location: app/.../ui/screen/WeatherTopBar.kt
// Function: Top action bar providing location drawer trigger, zero-telemetry
// badge, temperature unit toggle, and immediate tactile refresh button.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.ui.components.art.VectorMenuIcon
import mx.isgaar.myweather.ui.components.art.VectorRefreshIcon
import mx.isgaar.myweather.ui.motion.pressScale
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography

@Composable
fun WeatherTopBar(
    unit: TemperatureUnit,
    reducedMotion: Boolean,
    onOpenLocations: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onToggleUnit: () -> Unit,
    onRefresh: () -> Unit
) {
    val pal = LocalWeatherPalette.current
    val pill = RoundedCornerShape(16.dp)
    val locDesc = stringResource(R.string.cd_open_locations)
    val unitDesc = stringResource(R.string.cd_toggle_units)
    val refDesc = stringResource(R.string.cd_refresh_weather)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.semantics { contentDescription = locDesc }
                .clip(pill)
                .pressScale(reducedMotion, onOpenLocations)
                .padding(8.dp)
        ) { VectorMenuIcon(pal.textPrimary) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.clip(pill)
                    .background(pal.glassSurface)
                    .border(1.dp, pal.glassBorder, pill)
                    .pressScale(reducedMotion, onOpenPrivacy)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    stringResource(R.string.badge_zero_telemetry),
                    style = WeatherTypography.captionTabular,
                    color = pal.textPrimary
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.semantics { contentDescription = unitDesc }
                    .clip(pill)
                    .background(pal.glassSurface)
                    .border(1.dp, pal.glassBorder, pill)
                    .pressScale(reducedMotion, onToggleUnit)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (unit == TemperatureUnit.CELSIUS) "\u00B0C"
                    else "\u00B0F",
                    style = WeatherTypography.captionTabular,
                    fontWeight = FontWeight.SemiBold,
                    color = pal.textPrimary
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.semantics { contentDescription = refDesc }
                    .clip(pill)
                    .pressScale(reducedMotion, onRefresh)
                    .padding(8.dp)
            ) { VectorRefreshIcon(pal.textPrimary) }
        }
    }
}
