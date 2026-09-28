package com.camphub.api.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtFilter: JwtRequestFilter,
    private val authEntryPoint: RestAuthEntryPoint,
    private val accessDeniedHandler: RestAccessDeniedHandler
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(cfg: AuthenticationConfiguration): AuthenticationManager =
        cfg.authenticationManager

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling {
                it.authenticationEntryPoint(authEntryPoint)
                it.accessDeniedHandler(accessDeniedHandler)
            }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                // Role rules; ownership is checked in services
                it.requestMatchers(HttpMethod.POST, "/api/bootcamps").hasRole("PROVIDER")
                it.requestMatchers(HttpMethod.POST, "/api/bootcamps/*/programs").hasRole("PROVIDER")
                it.requestMatchers(HttpMethod.POST, "/api/programs/*/reviews").hasRole("USER")
                it.requestMatchers(HttpMethod.POST, "/api/threads").hasAnyRole("USER", "PROVIDER")
                it.requestMatchers(HttpMethod.POST, "/api/threads/*/comments").hasAnyRole("USER", "PROVIDER")
                // Everything else requires login
                it.anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
