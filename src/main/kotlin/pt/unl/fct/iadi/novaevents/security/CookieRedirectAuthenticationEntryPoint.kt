package pt.unl.fct.iadi.novaevents.security

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CookieRedirectAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authException: AuthenticationException
    ) {
        val originalTarget = buildOriginalTarget(request)
        val encoded = URLEncoder.encode(originalTarget, StandardCharsets.UTF_8)

        response.addCookie(
                Cookie(SecurityConstants.REDIRECT_COOKIE, encoded).apply {
                    path = "/"
                    isHttpOnly = true
                    maxAge = 300
                }
        )
        response.sendRedirect("/login")
    }

    private fun buildOriginalTarget(request: HttpServletRequest): String {
        val query = request.queryString
        return if (query.isNullOrBlank()) request.requestURI else "${request.requestURI}?$query"
    }
}
