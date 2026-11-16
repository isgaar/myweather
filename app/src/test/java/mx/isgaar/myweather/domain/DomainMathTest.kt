// Location: app/src/test/java/mx/isgaar/myweather/domain/DomainMathTest.kt
// Function: Unit tests verifying WMO condition mapping, Celsius to Fahrenheit
// conversions, solar horizon arc progress, and Orizaba coordinate defaults.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain

import mx.isgaar.myweather.domain.model.SavedLocation
import mx.isgaar.myweather.domain.model.TemperatureUnit
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.domain.port.SolarMath
import mx.isgaar.myweather.domain.port.UnitFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainMathTest {

    @Test
    fun `maps WMO weather codes to domain conditions`() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromWmoCode(0))
        assertEquals(WeatherCondition.FOG, WeatherCondition.fromWmoCode(45))
        assertEquals(
            WeatherCondition.RAIN_LIGHT,
            WeatherCondition.fromWmoCode(61)
        )
        assertEquals(WeatherCondition.STORM, WeatherCondition.fromWmoCode(95))
    }

    @Test
    fun `converts celsius to fahrenheit accurately`() {
        val f = UnitFormatter.convertTemp(23.0, TemperatureUnit.FAHRENHEIT)
        assertEquals(73.4, f, 0.1)
        assertEquals(
            "23°",
            UnitFormatter.formatTemp(23.4, TemperatureUnit.CELSIUS)
        )
        assertEquals(
            "73°",
            UnitFormatter.formatTemp(23.0, TemperatureUnit.FAHRENHEIT)
        )
    }

    @Test
    fun `computes solar arc progress between sunrise and sunset`() {
        val mid = SolarMath.dayProgress(
            nowMinutes = 12 * 60,
            sunriseMinutes = 6 * 60,
            sunsetMinutes = 18 * 60
        )
        assertEquals(0.5f, mid, 0.01f)
        val night = SolarMath.dayProgress(
            nowMinutes = 21 * 60,
            sunriseMinutes = 6 * 60,
            sunsetMinutes = 18 * 60
        )
        assertEquals(1.0f, night, 0.01f)
    }

    @Test
    fun `defaults to Orizaba Veracruz coordinates and elevation`() {
        val def = SavedLocation.ORIZABA_DEFAULT
        assertEquals("Orizaba", def.name)
        assertTrue(def.latitude in 18.8..18.9)
        assertTrue(def.longitude in -97.2..-97.0)
        assertTrue(def.elevationMeters > 1200)
    }
}
