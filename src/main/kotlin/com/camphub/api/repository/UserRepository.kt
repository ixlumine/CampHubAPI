package com.camphub.api.repository

import com.camphub.api.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    // Used by login
    fun findByEmail(email: String): User?

    // Used by register to block duplicate emails
    fun existsByEmail(email: String): Boolean
}
