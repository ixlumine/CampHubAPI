package com.camphub.api.service

import com.camphub.api.dto.AuthResponse
import com.camphub.api.dto.LoginRequest
import com.camphub.api.dto.RegisterRequest
import com.camphub.api.exception.BadRequestException
import com.camphub.api.exception.UnauthorizedException
import com.camphub.api.model.Role
import com.camphub.api.model.User
import com.camphub.api.repository.UserRepository
import com.camphub.api.security.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val authManager: AuthenticationManager
) {
    @Transactional
    fun register(req: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(req.email)) {
            throw BadRequestException("Email sudah terdaftar")
        }
        val u = User()
        u.name = req.name
        u.email = req.email
        u.password = encoder.encode(req.password)
        // Register always creates USER; the client cannot choose a role
        u.role = Role.USER
        userRepository.save(u)
        return u.toAuthResponse()
    }

    fun login(req: LoginRequest): AuthResponse {
        try {
            authManager.authenticate(UsernamePasswordAuthenticationToken(req.email, req.password))
        } catch (e: BadCredentialsException) {
            // salah email/password
            throw UnauthorizedException("Email atau password salah")
        } catch (e: Exception) {
            throw UnauthorizedException("Gagal autentikasi")
        }
        val u = userRepository.findByEmail(req.email)
            ?: throw UnauthorizedException("Email atau password salah")
        return u.toAuthResponse()
    }

    private fun User.toAuthResponse() =
        AuthResponse(
            token = jwtUtil.generateToken(email, mapOf("name" to name)),
            userId = id!!,
            name = name,
            role = role.name
        )
}
