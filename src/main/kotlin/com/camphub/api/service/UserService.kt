package com.camphub.api.service

import com.camphub.api.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.*
import org.springframework.stereotype.Service

// Loads a user for login checks; role becomes ROLE_<role>
@Service
class UserService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val u =
            userRepository.findByEmail(email)
                ?: throw UsernameNotFoundException("User with email=$email not found")
        return org.springframework.security.core.userdetails.User(
            u.email,
            u.password,
            listOf(SimpleGrantedAuthority("ROLE_" + u.role.name))
        )
    }
}
