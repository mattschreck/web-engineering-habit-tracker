package com.example.habittracker.controller

import com.example.habittracker.BaseIntegrationTest
import com.example.habittracker.dto.LoginRequest
import com.example.habittracker.dto.RegisterRequest
import com.example.habittracker.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class AuthControllerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @AfterEach
    fun cleanup() {
        userRepository.deleteAll()
    }

    @Test
    fun `should register new user successfully`() {
        val request = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.user.username").value("testuser"))
            .andExpect(jsonPath("$.user.email").value("test@example.com"))
            .andExpect(jsonPath("$.message").value("User registered successfully"))
            .andExpect(jsonPath("$.token").isNotEmpty)

        val savedUser = userRepository.findByUsername("testuser").orElse(null)
        assertNotNull(savedUser)
        assertEquals("testuser", savedUser.username)
        assertTrue(savedUser.password.startsWith("\$2a\$")) // BCrypt hash starts with $2a$
    }

    @Test
    fun `should fail registration when username already exists`() {
        val request = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Username 'testuser' is already taken"))
    }

    @Test
    fun `should fail registration when email already exists`() {
        val request1 = RegisterRequest(
            username = "testuser1",
            email = "test@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        )
            .andExpect(status().isCreated)

        val request2 = RegisterRequest(
            username = "testuser2",
            email = "test@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Email 'test@example.com' is already registered"))
    }

    @Test
    fun `should fail registration with invalid data`() {
        val request = RegisterRequest(
            username = "ab",
            email = "invalid-email",
            password = "123"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.validationErrors").exists())
    }

    @Test
    fun `should login successfully with valid credentials`() {
        val registerRequest = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )

        val loginRequest = LoginRequest(
            usernameOrEmail = "testuser",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.user.username").value("testuser"))
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.token").isNotEmpty)
    }

    @Test
    fun `should login successfully with email`() {
        val registerRequest = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )

        val loginRequest = LoginRequest(
            usernameOrEmail = "test@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.user.email").value("test@example.com"))
            .andExpect(jsonPath("$.token").isNotEmpty)
    }

    @Test
    fun `should fail login with invalid credentials`() {
        val loginRequest = LoginRequest(
            usernameOrEmail = "nonexistent",
            password = "wrongpassword"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("Invalid username/email or password"))
    }

    @Test
    fun `should fail login with wrong password`() {
        val registerRequest = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )

        val loginRequest = LoginRequest(
            usernameOrEmail = "testuser",
            password = "wrongpassword"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("Invalid username/email or password"))
    }
}
