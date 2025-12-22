package com.example.habittracker.dto

import com.example.habittracker.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class RegisterRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Username or email is required")
    val usernameOrEmail: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val enabled: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun fromEntity(user: User): UserResponse {
            return UserResponse(
                id = user.id!!,
                username = user.username,
                email = user.email,
                enabled = user.enabled,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
}

data class AuthResponse(
    val token: String?,
    val user: UserResponse,
    val message: String = "Authentication successful"
)
