// Location: app/.../ui/theme/MyWeatherTheme.kt
// Function: Root Compose theme providing dynamic atmospheric color tokens and
// Material 3 styling tailored for high-contrast weather readability.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import mx.isgaar.myweather.domain.model.WeatherCondition

@Composable
fun MyWeatherTheme(
    condition: WeatherCondition,
    isDay: Boolean,
    content: @Composable () -> Unit
) {
    val palette = rememberAtmosphericPalette(condition, isDay)
    val scheme = darkColorScheme(
        primary = palette.accentRain,
        background = palette.skyTop,
        surface = palette.sheetSurface,
        onBackground = palette.textPrimary,
        onSurface = palette.textPrimary
    )
    CompositionLocalProvider(LocalWeatherPalette provides palette) {
        MaterialTheme(
            colorScheme = scheme,
            content = content
        )
    }
}
