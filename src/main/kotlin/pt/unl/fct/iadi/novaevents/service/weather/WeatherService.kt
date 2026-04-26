package pt.unl.fct.iadi.novaevents.service.weather

import org.springframework.stereotype.Service

@Service
class WeatherService(private val weatherHttpExchange: WeatherHttpExchange) {

    fun isRaining(location: String): Boolean? {
        val response = weatherHttpExchange.fetchWeather(location.trim()) ?: return null
        val condition = response.weather.firstOrNull()?.main?.trim()?.lowercase() ?: return null

        return when (condition) {
            "rain", "drizzle", "thunderstorm" -> true
            "clear",
            "clouds",
            "mist",
            "smoke",
            "haze",
            "dust",
            "fog",
            "sand",
            "ash",
            "squall",
            "tornado" -> false
            else -> false
        }
    }
}
