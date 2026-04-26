package pt.unl.fct.iadi.novaevents.service.weather

interface WeatherHttpExchange {
    fun fetchWeather(location: String): WeatherApiResponse?
}
