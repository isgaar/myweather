// Location: app/.../ui/components/GlassWeatherCard.kt
// Function: Reusable translucent rounded glass card container implementing
// soft specular top borders and Apple-style visual layering.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette

@Composable
fun GlassWeatherCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = LocalWeatherPalette.current
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.glassSurface)
            .border(1.dp, palette.glassBorder, shape)
            .padding(16.dp),
        content = content
    )
}
