package com.camphub.api.config

import com.camphub.api.model.Role
import com.camphub.api.model.User
import com.camphub.api.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Order(1)
class UserSeeder(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    @Value("\${app.seed.admin-password}") private val adminPassword: String
) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(UserSeeder::class.java)

    override fun run(vararg args: String) {
        // Seed only when the table is empty, so restarts do not duplicate data
        if (userRepository.count() != 0L) return

        val samplePassword = "password123"
        // Keep this order; other seeders pick accounts by position
        val users = listOf(
            newUser("Admin CampHub", "admin@example.com", adminPassword, Role.ADMIN),
            newUser("Kode Nusantara", "kodenusantara@example.com", samplePassword, Role.PROVIDER),
            newUser("Rintis Edukasi", "rintis@example.com", samplePassword, Role.PROVIDER),
            newUser("Rina Wulandari", "rina@example.com", samplePassword, Role.USER),
            newUser("Bima Prasetyo", "bima@example.com", samplePassword, Role.USER),
            newUser("Sekar Ayu", "sekar@example.com", samplePassword, Role.USER)
        )
        userRepository.saveAll(users)
        log.info("UserSeeder: {} users created", users.size)
    }

    private fun newUser(name: String, email: String, password: String, role: Role): User {
        val u = User()
        u.name = name
        u.email = email
        u.password = encoder.encode(password)
        u.role = role
        return u
    }
}
