package com.camphub.api.security

import com.camphub.api.service.UserService
import jakarta.servlet.*
import jakarta.servlet.http.*
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtRequestFilter(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(JwtRequestFilter::class.java)

    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {
        val header = req.getHeader("Authorization")
        if (header.isNullOrBlank() || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res)
            return
        }

        val token = header.removePrefix("Bearer ").trim()

        val username = runCatching { jwtUtil.extractUsername(token) }
            .onFailure {
                // token ada tapi invalid/expired
                req.setAttribute("jwt_invalid", true)
                log.debug("JWT extractUsername failed: {}", it.message)
            }
            .getOrNull()

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = runCatching { userService.loadUserByUsername(username) }
                .onFailure {
                    // kalau user tidak ada, bisa dianggap token invalid juga
                    req.setAttribute("jwt_invalid", true)
                    log.debug("loadUserByUsername failed: {}", it.message)
                }
                .getOrNull()

            if (userDetails != null) {
                val valid = runCatching { jwtUtil.isTokenValid(token, userDetails.username) }
                    .onFailure {
                        req.setAttribute("jwt_invalid", true)
                        log.debug("JWT validation failed: {}", it.message)
                    }
                    .getOrNull() == true

                if (valid) {
                    val auth = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )
                    auth.details = WebAuthenticationDetailsSource().buildDetails(req)
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
        }

        chain.doFilter(req, res)
    }

    // Opsional: biar filter tidak jalan di /api/auth/**
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.servletPath.startsWith("/api/auth/")
    }
}
