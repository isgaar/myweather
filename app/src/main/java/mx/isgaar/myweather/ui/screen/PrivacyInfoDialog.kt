// Location: app/.../ui/screen/PrivacyInfoDialog.kt
// Function: Transparent privacy dialog confirming zero telemetry, zero keys,
// and strict established-location meteorological lookups via Open-Meteo.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import mx.isgaar.myweather.R
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette

@Composable
fun PrivacyInfoDialog(onDismiss: () -> Unit) {
    val palette = LocalWeatherPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = palette.sheetSurface,
        title = {
            Text(
                text = stringResource(R.string.privacy_title),
                color = palette.textPrimary
            )
        },
        text = {
            Text(
                text = stringResource(R.string.privacy_body),
                color = palette.textSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cd_close))
            }
        }
    )
}
