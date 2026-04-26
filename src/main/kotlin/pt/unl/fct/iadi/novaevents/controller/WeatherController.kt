package pt.unl.fct.iadi.novaevents.controller

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import pt.unl.fct.iadi.novaevents.service.weather.WeatherService
import pt.unl.fct.iadi.novaevents.service.weather.WeatherStatusResponse

@Controller
@RequestMapping("/api/weather")
class WeatherController(private val weatherService: WeatherService) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun weatherJson(@RequestParam location: String): WeatherStatusResponse {
        return WeatherStatusResponse(raining = weatherService.isRaining(location))
    }

    @GetMapping(produces = [MediaType.TEXT_HTML_VALUE])
    fun weatherHtml(@RequestParam location: String, model: Model): String {
        model.addAttribute("raining", weatherService.isRaining(location))
        return "weather/status :: status"
    }
}
