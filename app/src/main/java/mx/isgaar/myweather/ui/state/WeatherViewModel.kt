// Location: app/.../ui/state/WeatherViewModel.kt
// Function: Coordinates weather refreshes, established location updates, and
// zero-telemetry city searches while updating Android home screen widgets.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.isgaar.myweather.R
import mx.isgaar.myweather.data.local.WeatherLocalStore
import mx.isgaar.myweather.data.remote.OpenMeteoClient
import mx.isgaar.myweather.data.remote.WeatherRepositoryImpl
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.widget.data.WidgetUpdater

class WeatherViewModel(app: Application) : AndroidViewModel(app) {

    private val store = WeatherLocalStore(app)
    private val repo = WeatherRepositoryImpl(OpenMeteoClient(), store)
    private val _state = MutableStateFlow(
        WeatherUiState(
            activeLocation = store.loadEstablishedLocation(),
            savedLocations = store.loadSavedLocations(),
            snapshot = store.loadCachedSnapshot(),
            unit = store.loadTemperatureUnit()
        )
    )
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    init { refreshWeather() }

    fun refreshWeather() {
        val target = _state.value.activeLocation
        _state.update { it.copy(isLoading = true, errorResId = null) }
        viewModelScope.launch {
            repo.fetchWeather(target).fold(
                onSuccess = { snap ->
                    _state.update {
                        it.copy(
                            snapshot = snap, activeLocation = snap.location,
                            isLoading = false, isOfflineCached = false,
                            errorResId = null
                        )
                    }
                    WidgetUpdater.updateAll(getApplication())
                },
                onFailure = {
                    val hasC = _state.value.snapshot != null
                    val err = if (hasC) null else R.string.status_error_network
                    _state.update {
                        it.copy(
                            isLoading = false, isOfflineCached = hasC,
                            errorResId = err
                        )
                    }
                }
            )
        }
    }

    fun selectLocation(loc: SavedLocation) {
        store.saveEstablishedLocation(loc)
        _state.update {
            it.copy(
                activeLocation = loc,
                savedLocations = store.loadSavedLocations(),
                showLocationSheet = false, searchQuery = "",
                searchResults = emptyList(), locationNoticeResId = null
            )
        }
        refreshWeather()
    }

    fun toggleUnit() {
        val next = if (_state.value.unit == TemperatureUnit.CELSIUS) {
            TemperatureUnit.FAHRENHEIT
        } else TemperatureUnit.CELSIUS
        store.saveTemperatureUnit(next)
        _state.update { it.copy(unit = next) }
        WidgetUpdater.updateAll(getApplication())
    }

    fun setLocationSheet(open: Boolean) = _state.update {
        it.copy(showLocationSheet = open, locationNoticeResId = null)
    }

    fun setPrivacyDialog(open: Boolean) =
        _state.update { it.copy(showPrivacyDialog = open) }

    fun updateSearchQuery(q: String) =
        _state.update { it.copy(searchQuery = q, locationNoticeResId = null) }

    fun submitSearch() {
        val q = _state.value.searchQuery
        _state.update {
            it.copy(isSearching = true, locationNoticeResId = null)
        }
        viewModelScope.launch {
            val (list, err) = LocationSearchRunner.run(repo, q)
            _state.update {
                it.copy(
                    isSearching = false, searchResults = list,
                    locationNoticeResId = err
                )
            }
        }
    }

    fun setLocationNotice(resId: Int) =
        _state.update { it.copy(locationNoticeResId = resId) }
}
