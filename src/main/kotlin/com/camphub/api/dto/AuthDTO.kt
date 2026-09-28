package com.camphub.api.dto

import jakarta.validation.constraints.*

data class RegisterRequest(
    @field:NotBlank(message = "Nama tidak boleh kosong")
    @field:Size(min = 2, max = 100, message = "Nama harus 2–100 karakter")
    val name: String,

    @field:NotBlank(message = "Email tidak boleh kosong")
    @field:Email(message = "Format email tidak valid")
    @field:Size(max = 150, message = "Email maksimal 150 karakter")
    val email: String,

    @field:NotBlank(message = "Password tidak boleh kosong")
    @field:Size(min = 8, max = 72, message = "Password harus 8–72 karakter")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Email tidak boleh kosong")
    @field:Email(message = "Format email tidak valid")
    val email: String,

    @field:NotBlank(message = "Password tidak boleh kosong")
    val password: String
)

data class AuthResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val userId: Long,
    val name: String,
    val role: String
)
