// Location: app/.../ui/screen/WeatherMainScreen.kt
// Function: Primary weather dashboard screen combining atmospheric gradient,
// top bar, scrollable WeatherBodySection, offline banner, and modal sheets.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.ui.screen.sections.WeatherBodySection
import mx.isgaar.myweather.ui.state.WeatherUiState
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography

@Composable
fun WeatherMainScreen(
    state: WeatherUiState,
    reducedMotion: Boolean,
    onOpenLocations: () -> Unit,
    onCloseLocations: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onClosePrivacy: () -> Unit,
    onToggleUnit: () -> Unit,
    onRefresh: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSelectLocation: (SavedLocation) -> Unit,
    onUseDeviceLocation: () -> Unit
) {
    val pal = LocalWeatherPalette.current
    val grad = Brush.verticalGradient(listOf(pal.skyTop, pal.skyBottom))
    Box(Modifier.fillMaxSize().background(grad).systemBarsPadding()) {
        Column(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 6.dp)
        ) {
            WeatherTopBar(
                state.unit, reducedMotion, onOpenLocations,
                onOpenPrivacy, onToggleUnit, onRefresh
            )
            if (state.isOfflineCached) {
                Text(
                    text = stringResource(R.string.status_offline_cached),
                    style = WeatherTypography.captionTabular,
                    color = pal.accentWarm,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            val snap = state.snapshot
            if (snap != null) {
                WeatherBodySection(snap, state, onOpenLocations)
            } else if (state.errorResId != null) {
                ErrorRetryBlock(state.errorResId, onRefresh)
            } else {
                Box(Modifier.fillMaxWidth().height(300.dp), Alignment.Center) {
                    CircularProgressIndicator(color = pal.textPrimary)
                }
            }
            Spacer(Modifier.height(28.dp))
        }
    }
    if (state.showLocationSheet) {
        LocationPickerSheet(
            state, onCloseLocations, onQueryChange,
            onSearch, onSelectLocation, onUseDeviceLocation
        )
    }
    if (state.showPrivacyDialog) {
        PrivacyInfoDialog(onDismiss = onClosePrivacy)
    }
}

@Composable
private fun ErrorRetryBlock(resId: Int, onRetry: () -> Unit) {
    val pal = LocalWeatherPalette.current
    Column(
        Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(resId),
            style = WeatherTypography.cardTitle,
            color = pal.textPrimary
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry_btn)) }
    }
}
