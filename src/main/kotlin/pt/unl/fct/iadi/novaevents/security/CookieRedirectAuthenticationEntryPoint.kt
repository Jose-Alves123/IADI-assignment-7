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
        val loginUrl = buildLoginUrl(request)

        response.addCookie(
                Cookie(SecurityConstants.REDIRECT_COOKIE, encoded).apply {
                    path = "/"
                    isHttpOnly = true
                    maxAge = 300
                }
        )
        response.sendRedirect(loginUrl)
    }

    private fun buildOriginalTarget(request: HttpServletRequest): String {
        val query = request.queryString
        return if (query.isNullOrBlank()) request.requestURI else "${request.requestURI}?$query"
    }

    private fun buildLoginUrl(request: HttpServletRequest): String {
        val requestUrl = request.requestURL.toString()
        val requestUri = request.requestURI
        val baseUrl = requestUrl.removeSuffix(requestUri)
        return "${baseUrl}${request.contextPath}/login"
    }
}
