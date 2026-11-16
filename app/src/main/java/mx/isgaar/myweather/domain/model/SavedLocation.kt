// Location: app/.../domain/model/SavedLocation.kt
// Function: Defines immutable geographic location data and regional Veracruz
// presets with Orizaba as the default zero-telemetry starting point.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain.model

data class SavedLocation(
    val id: String,
    val name: String,
    val region: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val elevationMeters: Int,
    val timezone: String = "America/Mexico_City"
) {
    fun displayLabel(): String {
        return if (region.isBlank()) name else "$name, $region"
    }

    companion object {
        val ORIZABA_DEFAULT = SavedLocation(
            id = "orizaba-ver-mx",
            name = "Orizaba",
            region = "Veracruz",
            country = "Mexico",
            latitude = 18.85195,
            longitude = -97.09957,
            elevationMeters = 1238
        )

        val ALTAS_MONTANAS_PRESETS = listOf(
            ORIZABA_DEFAULT,
            SavedLocation(
                id = "cordoba-ver-mx",
                name = "Cordoba",
                region = "Veracruz",
                country = "Mexico",
                latitude = 18.8942,
                longitude = -96.9356,
                elevationMeters = 860
            ),
            SavedLocation(
                id = "fortin-ver-mx",
                name = "Fortin de las Flores",
                region = "Veracruz",
                country = "Mexico",
                latitude = 18.9022,
                longitude = -96.9994,
                elevationMeters = 1015
            ),
            SavedLocation(
                id = "xalapa-ver-mx",
                name = "Xalapa",
                region = "Veracruz",
                country = "Mexico",
                latitude = 19.5438,
                longitude = -96.9102,
                elevationMeters = 1417
            ),
            SavedLocation(
                id = "veracruz-puerto-mx",
                name = "Veracruz",
                region = "Veracruz",
                country = "Mexico",
                latitude = 19.1738,
                longitude = -96.1342,
                elevationMeters = 10
            )
        )
    }
}
