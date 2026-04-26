package pt.unl.fct.iadi.novaevents.controller

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pt.unl.fct.iadi.novaevents.service.weather.WeatherService

@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @MockBean lateinit var weatherService: WeatherService

    @Test
    @WithMockUser
    fun jsonWeatherEndpointReturnsBoolean() {
        org.mockito.Mockito.`when`(weatherService.isRaining("Lisbon")).thenReturn(false)

        mockMvc.perform(
                        get("/api/weather")
                                .param("location", "Lisbon")
                                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk)
                .andExpect(content().json("{\"raining\":false}"))
    }

    @Test
    @WithMockUser
    fun htmlWeatherEndpointReturnsFragment() {
        org.mockito.Mockito.`when`(weatherService.isRaining("Lisbon")).thenReturn(true)

        mockMvc.perform(
                        get("/api/weather")
                                .param("location", "Lisbon")
                                .accept(org.springframework.http.MediaType.TEXT_HTML)
                )
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Raining")))
    }
}
