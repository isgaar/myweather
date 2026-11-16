// Location: app/.../ui/screen/HighlightsPagerCard.kt
// Function: Horizontal swipeable pager card displaying four local atmospheric
// telemetry-free metric slides tailored for mountain and valley weather.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.WeatherSnapshot
import mx.isgaar.myweather.domain.port.UnitFormatter
import mx.isgaar.myweather.ui.components.GlassWeatherCard
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography
import java.util.Locale

@Composable
fun HighlightsPagerCard(snapshot: WeatherSnapshot) {
    val pal = LocalWeatherPalette.current
    val pages = buildPages(snapshot)
    val pager = rememberPagerState(pageCount = { pages.size })

    GlassWeatherCard {
        HorizontalPager(state = pager) { idx ->
            val (title, body) = pages[idx]
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title, style = WeatherTypography.cardTitle,
                    fontWeight = FontWeight.SemiBold, color = pal.textPrimary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    body, style = WeatherTypography.bodyTabular,
                    color = pal.textSecondary
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            repeat(pages.size) { index ->
                val dotCol = if (pager.currentPage == index) pal.textPrimary
                else pal.textMuted.copy(alpha = 0.45f)
                Box(Modifier.size(7.dp).clip(CircleShape).background(dotCol))
            }
        }
    }
}

@Composable
private fun buildPages(s: WeatherSnapshot): List<Pair<String, String>> {
    val c = s.current
    val dir = UnitFormatter.cardinalDirection(c.windDirectionDeg)
    val uvLbl = stringResource(UnitFormatter.uvLabelRes(c.uvIndex))
    val uvS = String.format(Locale.US, "%.1f", c.uvIndex)
    val visS = String.format(Locale.US, "%.1f", c.visibilityKm)
    val aq = s.airQuality
    val air = if (aq != null) {
        val lbl = stringResource(UnitFormatter.aqiLabelRes(aq.europeanAqi))
        val pm = String.format(Locale.US, "%.1f", aq.pm25UgM3)
        stringResource(R.string.highlight_air_body_fmt, aq.europeanAqi, lbl, pm)
    } else {
        stringResource(
            R.string.highlight_air_na_fmt, s.location.elevationMeters
        )
    }
    return listOf(
        stringResource(R.string.highlight_wind_title) to stringResource(
            R.string.highlight_wind_body_fmt, dir,
            "${c.windSpeedKmh} km/h", "${c.windGustsKmh} km/h"
        ),
        stringResource(R.string.highlight_humidity_title) to stringResource(
            R.string.highlight_humidity_body_fmt,
            c.humidityPct, c.surfacePressureHpa
        ),
        stringResource(R.string.highlight_uv_title) to stringResource(
            R.string.highlight_uv_body_fmt, uvS, uvLbl, visS
        ),
        stringResource(R.string.highlight_air_title) to air
    )
}
