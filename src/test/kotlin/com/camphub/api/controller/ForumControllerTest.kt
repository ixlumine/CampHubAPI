package com.camphub.api.controller

import com.camphub.api.model.Role
import com.camphub.api.model.User
import com.camphub.api.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ForumControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    private val testUserEmail = "forumuser@example.com"

    @BeforeEach
    fun setUp() {
        if (userRepository.findByEmail(testUserEmail) == null) {
            val user = User().apply {
                name = "Forum User"
                email = testUserEmail
                password = "\$2a\$10\$encryptedPassword"
                role = Role.USER
            }
            userRepository.save(user)
        }
    }

}
