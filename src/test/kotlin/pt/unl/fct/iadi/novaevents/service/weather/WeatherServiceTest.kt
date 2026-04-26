package pt.unl.fct.iadi.novaevents.service.weather

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class WeatherServiceTest {

    private class FakeWeatherHttpExchange(private val response: WeatherApiResponse?) :
            WeatherHttpExchange {
        override fun fetchWeather(location: String): WeatherApiResponse? = response
    }

    @Test
    fun returnsTrueWhenWeatherIsRain() {
        val service =
                WeatherService(
                        FakeWeatherHttpExchange(
                                WeatherApiResponse(listOf(WeatherConditionDto(main = "Rain")))
                        )
                )

        assertEquals(true, service.isRaining(" Lisbon "))
    }

    @Test
    fun returnsFalseWhenWeatherIsClear() {
        val service =
                WeatherService(
                        FakeWeatherHttpExchange(
                                WeatherApiResponse(listOf(WeatherConditionDto(main = "Clear")))
                        )
                )

        assertEquals(false, service.isRaining("Lisbon"))
    }

    @Test
    fun returnsNullWhenWeatherUnavailable() {
        val service = WeatherService(FakeWeatherHttpExchange(null))

        assertNull(service.isRaining("Lisbon"))
    }
}
