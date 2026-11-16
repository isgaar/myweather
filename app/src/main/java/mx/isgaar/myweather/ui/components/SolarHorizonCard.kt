// Location: app/.../ui/components/SolarHorizonCard.kt
// Function: Draws the solar cycle card featuring a smooth sine-wave horizon
// curve, glowing solar marker dot, and localized sunrise and sunset clocks.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.port.SolarMath
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SolarHorizonCard(
    sunriseIso: String,
    sunsetIso: String
) {
    val palette = LocalWeatherPalette.current
    val sunriseMin = SolarMath.parseIsoMinutes(sunriseIso) ?: (6 * 60 + 30)
    val sunsetMin = SolarMath.parseIsoMinutes(sunsetIso) ?: (17 * 60 + 50)
    val now = LocalTime.now()
    val progress = SolarMath.dayProgress(
        nowMinutes = now.hour * 60 + now.minute,
        sunriseMinutes = sunriseMin,
        sunsetMinutes = sunsetMin
    )
    val sunriseText = SolarMath.formatIsoClock(sunriseIso)
    val sunsetText = SolarMath.formatIsoClock(sunsetIso)

    GlassWeatherCard {
        Text(
            text = stringResource(R.string.section_solar_title),
            style = WeatherTypography.cardTitle,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            val w = size.width
            val h = size.height
            val horizonY = h * 0.62f
            drawLine(
                color = palette.textMuted.copy(alpha = 0.45f),
                start = Offset(0f, horizonY),
                end = Offset(w, horizonY),
                strokeWidth = 2f
            )
            val path = Path()
            val steps = 64
            for (i in 0..steps) {
                val t = i.toFloat() / steps
                val x = w * t
                val y = horizonY - sin(t * PI).toFloat() * (h * 0.44f)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = palette.textSecondary.copy(alpha = 0.7f),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
            val sunX = w * progress
            val sunY = horizonY - sin(progress * PI).toFloat() * (h * 0.44f)
            drawCircle(
                color = palette.accentSun.copy(alpha = 0.30f),
                radius = 18f,
                center = Offset(sunX, sunY)
            )
            drawCircle(
                color = palette.textPrimary,
                radius = 7f,
                center = Offset(sunX, sunY)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.sunrise_value_fmt, sunriseText),
                style = WeatherTypography.bodyTabular,
                fontWeight = FontWeight.Medium,
                color = palette.textPrimary
            )
            Text(
                text = stringResource(R.string.sunset_value_fmt, sunsetText),
                style = WeatherTypography.bodyTabular,
                fontWeight = FontWeight.Medium,
                color = palette.textPrimary
            )
        }
    }
}
