// Location: app/.../ui/state/LocationSearchRunner.kt
// Function: Executes zero-telemetry Open-Meteo city search requests on behalf
// of WeatherViewModel and returns localized error resources when empty.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.state

import mx.isgaar.myweather.R
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.port.WeatherRepository
import java.util.Locale

internal object LocationSearchRunner {

    suspend fun run(
        repo: WeatherRepository,
        query: String
    ): Pair<List<SavedLocation>, Int?> {
        val lang = if (Locale.getDefault().language == "en") "en" else "es"
        val list = repo.searchLocations(query, lang).getOrDefault(emptyList())
        val err = if (list.isEmpty()) R.string.locations_no_results else null
        return list to err
    }
}

