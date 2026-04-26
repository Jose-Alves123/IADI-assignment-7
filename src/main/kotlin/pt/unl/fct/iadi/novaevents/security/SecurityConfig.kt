package pt.unl.fct.iadi.novaevents.security

import jakarta.servlet.http.Cookie
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
        private val jwtAuthenticationFilter: JwtAuthenticationFilter,
        private val cookieRedirectAuthenticationEntryPoint: CookieRedirectAuthenticationEntryPoint,
        private val jwtLoginSuccessHandler: JwtLoginSuccessHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
                .csrf { it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .requestCache { it.disable() }
                .httpBasic { it.disable() }
                .formLogin {
                    it.loginPage("/login")
                    it.loginProcessingUrl("/login")
                    it.successHandler(jwtLoginSuccessHandler)
                    it.failureUrl("/login?error")
                    it.permitAll()
                }
                .logout {
                    it.logoutUrl("/logout")
                    it.addLogoutHandler { _, response, _ ->
                        response.addCookie(
                                Cookie(SecurityConstants.JWT_COOKIE, "").apply {
                                    path = "/"
                                    isHttpOnly = true
                                    maxAge = 0
                                }
                        )
                    }
                    it.logoutSuccessUrl("/clubs")
                }
                .authorizeHttpRequests {
                    it.requestMatchers("/login", "/error", "/error/**", "/favicon.ico").permitAll()

                    it.requestMatchers(
                                    HttpMethod.GET,
                                    "/",
                                    "/clubs",
                                    "/clubs/*",
                                    "/events",
                                    "/events/*",
                                    "/clubs/*/events/*"
                            )
                            .permitAll()

                    it.requestMatchers(
                                    HttpMethod.GET,
                                    "/clubs/*/events/new",
                                    "/clubs/*/events/*/edit",
                                    "/events/*/edit"
                            )
                            .hasAnyRole("EDITOR", "ADMIN")

                    it.requestMatchers(HttpMethod.POST, "/clubs/*/events")
                            .hasAnyRole("EDITOR", "ADMIN")
                    it.requestMatchers(
                                    HttpMethod.PUT,
                                    "/clubs/*/events/*",
                                    "/clubs/*/events/*/edit",
                                    "/events/*",
                                    "/events/*/edit"
                            )
                            .hasAnyRole("EDITOR", "ADMIN")

                    it.requestMatchers(
                                    HttpMethod.GET,
                                    "/clubs/*/events/*/delete",
                                    "/events/*/delete"
                            )
                            .hasRole("ADMIN")
                    it.requestMatchers(
                                    HttpMethod.DELETE,
                                    "/clubs/*/events/*",
                                    "/clubs/*/events/*/delete",
                                    "/events/*",
                                    "/events/*/delete"
                            )
                            .hasRole("ADMIN")

                    it.anyRequest().authenticated()
                }
                .exceptionHandling {
                    it.authenticationEntryPoint(cookieRedirectAuthenticationEntryPoint)
                }
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter::class.java
                )

        return http.build()
    }

    @Bean
    fun authenticationProvider(
            userDetailsService: UserDetailsService,
            passwordEncoder: PasswordEncoder
    ): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

    @Bean
    fun authenticationManager(
            authenticationConfiguration: AuthenticationConfiguration
    ): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
