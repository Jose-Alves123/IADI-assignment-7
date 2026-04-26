package pt.unl.fct.iadi.novaevents.service.weather

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class OpenWeatherMapWeatherHttpExchange(
        builder: RestClient.Builder,
        @Value("\${weather.api.key}") private val apiKey: String
) : WeatherHttpExchange {

    private val restClient: RestClient =
            builder.baseUrl("https://api.openweathermap.org/data/2.5").build()

    override fun fetchWeather(location: String): WeatherApiResponse? {
        return runCatching {
                    restClient
                            .get()
                            .uri { uriBuilder ->
                                uriBuilder
                                        .path("/weather")
                                        .queryParam("q", location)
                                        .queryParam("appid", apiKey)
                                        .build()
                            }
                            .retrieve()
                            .body(WeatherApiResponse::class.java)
                }
                .getOrNull()
    }
}
