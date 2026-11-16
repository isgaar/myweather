// Location: app/.../ui/components/art/ThermalBar.kt
// Function: Renders gradient thermal range bars and localized day-offset labels
// for multi-day weather forecast rows.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components.art

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.DailyForecast
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ThermalBar(
    startF: Float,
    widthF: Float,
    rem: Float,
    modifier: Modifier
) {
    val pal = LocalWeatherPalette.current
    val pill = RoundedCornerShape(3.dp)
    Box(modifier.height(6.dp).clip(pill).background(pal.glassStrong)) {
        Row(Modifier.fillMaxWidth()) {
            if (startF > 0f) Spacer(Modifier.weight(startF))
            Box(
                Modifier.weight(widthF).fillMaxHeight().clip(pill).background(
                    Brush.horizontalGradient(
                        listOf(pal.accentCool, pal.accentWarm, pal.accentHot)
                    )
                )
            )
            if (rem > 0f) Spacer(Modifier.weight(rem))
        }
    }
}

@Composable
fun resolveDayLabel(day: DailyForecast): String = when (day.dayIndexOffset) {
    -1 -> stringResource(R.string.yesterday_label)
    0 -> stringResource(R.string.today_label)
    else -> runCatching {
        LocalDate.parse(day.isoDate).dayOfWeek
            .getDisplayName(TextStyle.SHORT, Locale.getDefault())
            .replaceFirstChar { it.uppercase() }
    }.getOrDefault(day.isoDate.takeLast(5))
}

