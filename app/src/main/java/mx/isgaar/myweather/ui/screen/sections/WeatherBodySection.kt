// Location: app/.../ui/screen/sections/WeatherBodySection.kt
// Function: Composes the scrollable body cards of the main weather screen
// including hero, hourly curve, highlights pager, solar arc, and daily rows.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.screen.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.ui.components.DailyForecastCard
import mx.isgaar.myweather.ui.components.HourlyCurveCard
import mx.isgaar.myweather.ui.components.SolarHorizonCard
import mx.isgaar.myweather.ui.screen.HighlightsPagerCard
import mx.isgaar.myweather.ui.state.WeatherUiState
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography

@Composable
fun WeatherBodySection(
    snap: WeatherSnapshot,
    state: WeatherUiState,
    onOpenLocations: () -> Unit
) {
    val pal = LocalWeatherPalette.current
    val today = snap.todayForecast()
    val condLbl = stringResource(snap.current.condition.labelRes)
    val hi = today?.let {
        UnitFormatter.formatTemp(it.tempMaxC, state.unit)
    } ?: "--"
    val lo = today?.let {
        UnitFormatter.formatTemp(it.tempMinC, state.unit)
    } ?: "--"

    WeatherHeroSection(snap, state.unit, condLbl, hi, lo, onOpenLocations)
    HourlyCurveCard(
        stringResource(R.string.hourly_summary_fmt, condLbl, hi, lo),
        snap.hourly,
        state.unit
    )
    Spacer(Modifier.height(14.dp))
    HighlightsPagerCard(snap)
    if (today != null) {
        Spacer(Modifier.height(14.dp))
        SolarHorizonCard(today.sunriseIso, today.sunsetIso)
    }
    Spacer(Modifier.height(14.dp))
    DailyForecastCard(snap.daily, state.unit)
    Spacer(Modifier.height(14.dp))
    Text(
        text = stringResource(R.string.source_footer_fmt, snap.updatedLabel),
        style = WeatherTypography.captionTabular,
        color = pal.textSecondary
    )
}

