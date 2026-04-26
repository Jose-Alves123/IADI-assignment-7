package pt.unl.fct.iadi.novaevents.security

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class JwtLoginSuccessHandler(private val jwtService: JwtService) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authentication: Authentication
    ) {
        val roles = authentication.authorities.map { it.authority }
        val jwt = jwtService.generateToken(authentication.name, roles)

        response.addCookie(
                Cookie(SecurityConstants.JWT_COOKIE, jwt).apply {
                    path = "/"
                    isHttpOnly = true
                    maxAge = 8 * 60 * 60
                }
        )

        val redirect = readRedirectCookie(request)
        response.addCookie(
                Cookie(SecurityConstants.REDIRECT_COOKIE, "").apply {
                    path = "/"
                    isHttpOnly = true
                    maxAge = 0
                }
        )

        response.sendRedirect(redirect ?: "/clubs")
    }

    private fun readRedirectCookie(request: HttpServletRequest): String? {
        val encoded =
                request.cookies?.firstOrNull { it.name == SecurityConstants.REDIRECT_COOKIE }?.value
                        ?: return null
        val decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8)
        if (!decoded.startsWith("/")) return null
        if (decoded.startsWith("//")) return null
        if (decoded.startsWith("/login") || decoded.startsWith("/logout")) return null
        return decoded
    }
}
