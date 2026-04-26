package pt.unl.fct.iadi.novaevents.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
        private val jwtService: JwtService,
        private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        val jwt = request.cookies?.firstOrNull { it.name == SecurityConstants.JWT_COOKIE }?.value
        if (!jwt.isNullOrBlank() && SecurityContextHolder.getContext().authentication == null) {
            runCatching {
                val claims = jwtService.parseClaims(jwt)
                val username = claims.subject
                if (!username.isNullOrBlank()) {
                    val userDetails = userDetailsService.loadUserByUsername(username)
                    val authentication =
                            UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.authorities
                            )
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
                    .onFailure {
                        response.addCookie(
                                Cookie(SecurityConstants.JWT_COOKIE, "").apply {
                                    isHttpOnly = true
                                    path = "/"
                                    maxAge = 0
                                }
                        )
                    }
        }

        filterChain.doFilter(request, response)
    }
}
