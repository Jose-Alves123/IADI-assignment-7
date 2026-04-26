package pt.unl.fct.iadi.novaevents.service.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherApiResponse(val weather: List<WeatherConditionDto> = emptyList())

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherConditionDto(val main: String? = null)

data class WeatherStatusResponse(val raining: Boolean?)
