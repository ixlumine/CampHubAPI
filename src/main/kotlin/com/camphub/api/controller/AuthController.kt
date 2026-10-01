package com.camphub.api.controller

import com.camphub.api.dto.AuthResponse
import com.camphub.api.dto.LoginRequest
import com.camphub.api.dto.RegisterRequest
import com.camphub.api.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

// Register and login; no token needed
@RestController
@RequestMapping("/api/auth")
class AuthController(private val service: AuthService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody req: RegisterRequest): AuthResponse = service.register(req)

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): AuthResponse = service.login(req)
}
