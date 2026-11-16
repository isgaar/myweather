// Location: app/.../ui/state/WeatherUiState.kt
// Function: Immutable Compose UI state representing active weather snapshot,
// established location, unit preference, search results, and sheet visibility.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.state

import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherSnapshot

data class WeatherUiState(
    val activeLocation: SavedLocation = SavedLocation.ORIZABA_DEFAULT,
    val savedLocations: List<SavedLocation> = listOf(
        SavedLocation.ORIZABA_DEFAULT
    ),
    val snapshot: WeatherSnapshot? = null,
    val unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isLoading: Boolean = true,
    val isOfflineCached: Boolean = false,
    val errorResId: Int? = null,
    val showLocationSheet: Boolean = false,
    val showPrivacyDialog: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SavedLocation> = emptyList(),
    val isSearching: Boolean = false,
    val locationNoticeResId: Int? = null
)
