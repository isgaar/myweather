// Location: app/.../ui/screen/LocationPickerSheet.kt
// Function: Bottom sheet allowing users to search cities, pick Altas Montanas
// presets, or trigger a one-shot device location check with zero tracking.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.ui.screen.sections.LocationRow
import mx.isgaar.myweather.ui.state.WeatherUiState
import mx.isgaar.myweather.ui.theme.LocalWeatherPalette
import mx.isgaar.myweather.ui.theme.WeatherTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerSheet(
    state: WeatherUiState,
    onDismiss: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSelectLocation: (SavedLocation) -> Unit,
    onUseDeviceLocation: () -> Unit
) {
    val pal = LocalWeatherPalette.current
    ModalBottomSheet(onDismiss, containerColor = pal.sheetSurface) {
        val colMod = Modifier.fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
        Column(colMod) {
            Text(
                text = stringResource(R.string.locations_title),
                style = WeatherTypography.cityTitle,
                color = pal.textPrimary
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(stringResource(R.string.locations_search_hint))
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onSearch, enabled = !state.isSearching) {
                    Text(stringResource(R.string.locations_search_btn))
                }
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onUseDeviceLocation, Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.locations_use_device))
            }
            state.locationNoticeResId?.let { id ->
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(id),
                    style = WeatherTypography.captionTabular,
                    color = pal.accentWarm
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.height(280.dp)) {
                if (state.searchResults.isNotEmpty()) {
                    items(state.searchResults, key = { it.id }) { loc ->
                        LocationRow(loc, onClick = { onSelectLocation(loc) })
                    }
                } else {
                    item(contentType = "header") {
                        Text(
                            stringResource(R.string.locations_presets_title),
                            style = WeatherTypography.captionTabular,
                            color = pal.textSecondary,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    items(
                        items = SavedLocation.ALTAS_MONTANAS_PRESETS,
                        key = { "pre-${it.id}" },
                        contentType = { "loc" }
                    ) { loc ->
                        LocationRow(loc, onClick = { onSelectLocation(loc) })
                    }
                }
            }
        }
    }
}
