package pt.unl.fct.iadi.novaevents.security

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class CookieRedirectAuthenticationEntryPointTest {

    @Test
    fun commenceRedirectsToAbsoluteLoginUrl() {
        val entryPoint = CookieRedirectAuthenticationEntryPoint()
        val request = MockHttpServletRequest("PUT", "/clubs/1/events/3/edit")
        val response = MockHttpServletResponse()

        entryPoint.commence(
                request,
                response,
                object : org.springframework.security.core.AuthenticationException("boom") {}
        )

        assertTrue(response.redirectedUrl!!.endsWith("/login"))
        assertTrue(response.cookies.any { it.name == SecurityConstants.REDIRECT_COOKIE })
    }
}
