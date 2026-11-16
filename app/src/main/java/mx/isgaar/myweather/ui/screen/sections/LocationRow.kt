// Location: app/.../ui/screen/sections/LocationRow.kt
// Function: Renders a selectable city item row inside the location picker sheet
// showing city name, region, country, and mountain elevation in meters.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.screen.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography

@Composable
fun LocationRow(location: SavedLocation, onClick: () -> Unit) {
    val pal = LocalWeatherPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(pal.glassSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.name,
                style = WeatherTypography.cardTitle,
                fontWeight = FontWeight.SemiBold,
                color = pal.textPrimary
            )
            Text(
                text = "${location.region}, ${location.country}",
                style = WeatherTypography.captionTabular,
                color = pal.textSecondary
            )
        }
        Text(
            text = "${location.elevationMeters} m",
            style = WeatherTypography.captionTabular,
            color = pal.textMuted
        )
    }
}

