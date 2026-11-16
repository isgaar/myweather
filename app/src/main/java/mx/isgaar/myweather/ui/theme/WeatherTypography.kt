// Location: app/.../ui/theme/WeatherTypography.kt
// Function: Defines optical Inter typography with negative tracking for display
// temperatures and tabular figures for aligned hourly and daily weather data.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mx.isgaar.myweather.R

object WeatherTypography {
    private val interFamily = FontFamily(
        Font(R.font.inter_regular, FontWeight.Light),
        Font(R.font.inter_regular, FontWeight.Normal),
        Font(R.font.inter_semibold, FontWeight.Medium),
        Font(R.font.inter_semibold, FontWeight.SemiBold),
        Font(R.font.inter_semibold, FontWeight.Bold)
    )

    val heroTemp = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Light,
        fontSize = 84.sp,
        lineHeight = 88.sp,
        letterSpacing = (-2.0).sp,
        fontFeatureSettings = "tnum"
    )
    val conditionTitle = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 28.sp
    )
    val cityTitle = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
    val cardTitle = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp
    )
    val bodyTabular = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontFeatureSettings = "tnum"
    )
    val captionTabular = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontFeatureSettings = "tnum"
    )
}