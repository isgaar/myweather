// Location: app/src/test/java/mx/isgaar/myweather/data/OpenMeteoParserTest.kt
// Function: Unit tests validating Open-Meteo forecast, air-quality, and
// geocoding JSON boundary parsers plus snapshot JSON serialization roundtrip.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data

import mx.isgaar.myweather.data.local.SnapshotJsonCodec
import mx.isgaar.myweather.data.remote.OpenMeteoAirParser
import mx.isgaar.myweather.data.remote.OpenMeteoForecastParser
import mx.isgaar.myweather.data.remote.OpenMeteoGeocodingParser
import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.model.WeatherError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenMeteoParserTest {

    @Test
    fun `parses valid Orizaba forecast JSON including yesterday and hourly`() {
        val json = """
        {
          "elevation": 1243.0,
          "current": {
            "time": "2026-11-15T13:30",
            "temperature_2m": 23.3,
            "relative_humidity_2m": 66,
            "apparent_temperature": 25.9,
            "is_day": 1,
            "precipitation": 0.0,
            "weather_code": 0,
            "cloud_cover": 9,
            "surface_pressure": 881.8,
            "wind_speed_10m": 5.8,
            "wind_gusts_10m": 24.5,
            "wind_direction_10m": 104
          },
          "hourly": {
            "time": ["2026-11-15T13:00","2026-11-15T14:00"],
            "temperature_2m": [23.0, 24.5],
            "precipitation_probability": [5, 10],
            "precipitation": [0.0, 0.0],
            "weather_code": [0, 2],
            "is_day": [1, 1],
            "uv_index": [6.2, 5.1],
            "visibility": [24000.0, 22000.0]
          },
          "daily": {
            "time": ["2026-11-14","2026-11-15"],
            "weather_code": [3, 0],
            "temperature_2m_max": [22.1, 25.4],
            "temperature_2m_min": [12.0, 13.2],
            "sunrise": ["2026-11-14T06:33","2026-11-15T06:34"],
            "sunset": ["2026-11-14T17:51","2026-11-15T17:51"],
            "uv_index_max": [5.0, 6.8],
            "precipitation_sum": [1.2, 0.0],
            "precipitation_probability_max": [40, 10],
            "wind_speed_10m_max": [11.0, 14.0]
          }
        }
        """.trimIndent()
        val snap = OpenMeteoForecastParser.parse(
            jsonText = json,
            location = SavedLocation.ORIZABA_DEFAULT,
            airQuality = OpenMeteoAirParser.parse(
                """{"current":{"european_aqi":18,"pm2_5":4.2}}"""
            ),
            nowEpochMs = 1_794_771_000_000L,
            updatedLabel = "13:30"
        )
        assertEquals(23.3, snap.current.temperatureC, 0.01)
        assertEquals(882, snap.current.surfacePressureHpa)
        assertEquals(WeatherCondition.CLEAR, snap.current.condition)
        assertEquals(2, snap.daily.size)
        assertEquals(-1, snap.daily[0].dayIndexOffset)
        assertEquals(0, snap.daily[1].dayIndexOffset)
        assertNotNull(snap.airQuality)
        assertEquals(18, snap.airQuality?.europeanAqi)

        val encoded = SnapshotJsonCodec.encode(snap)
        val decoded = SnapshotJsonCodec.decode(encoded)
        assertNotNull(decoded)
        assertEquals(
            snap.current.temperatureC,
            decoded!!.current.temperatureC,
            0.01
        )
        assertEquals(snap.daily.size, decoded.daily.size)
    }

    @Test(expected = WeatherError.InvalidPayload::class)
    fun `rejects malformed forecast payload without current block`() {
        OpenMeteoForecastParser.parse(
            jsonText = """{"hourly":{}}""",
            location = SavedLocation.ORIZABA_DEFAULT,
            airQuality = null,
            nowEpochMs = 0L,
            updatedLabel = "--:--"
        )
    }

    @Test
    fun `parses geocoding results for Orizaba`() {
        val raw = """
        {"results":[
          {"id":3522307,"name":"Orizaba","latitude":18.85195,
           "longitude":-97.09957,"elevation":1238.0,
           "country":"Mexico","admin1":"Veracruz",
           "timezone":"America/Mexico_City"}
        ]}
        """.trimIndent()
        val list = OpenMeteoGeocodingParser.parse(raw)
        assertEquals(1, list.size)
        assertEquals("Orizaba", list[0].name)
        assertTrue(list[0].elevationMeters == 1238)
    }
}
