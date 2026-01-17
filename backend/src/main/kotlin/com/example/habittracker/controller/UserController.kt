package com.example.habittracker.controller

import com.example.habittracker.dto.AuthResponse
import com.example.habittracker.dto.UpdateUserRequest
import com.example.habittracker.dto.UserResponse
import com.example.habittracker.exception.ResourceNotFoundException
import com.example.habittracker.exception.UnauthorizedException
import com.example.habittracker.security.JwtTokenProvider
import com.example.habittracker.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user's profile")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            ApiResponse(responseCode = "401", description = "Not authenticated"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val username = getCurrentUsername()
        val user = userService.findByUsername(username)
            ?: throw ResourceNotFoundException("User not found")

        return ResponseEntity.ok(UserResponse.fromEntity(user))
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user", description = "Updates the currently authenticated user's username. Returns a new JWT token.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User successfully updated"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "401", description = "Not authenticated"),
            ApiResponse(responseCode = "404", description = "User not found"),
            ApiResponse(responseCode = "409", description = "Username already in use")
        ]
    )
    fun updateCurrentUser(
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<AuthResponse> {
        val username = getCurrentUsername()
        val user = userService.findByUsername(username)
            ?: throw ResourceNotFoundException("User not found")

        val updatedUser = userService.updateCurrentUser(user, request)
        val newToken = jwtTokenProvider.generateTokenFromUsername(updatedUser.username)

        val response = AuthResponse(
            token = newToken,
            user = UserResponse.fromEntity(updatedUser),
            message = "Profile updated successfully"
        )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete current user", description = "Deletes the currently authenticated user's account and all associated data")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "User successfully deleted"),
            ApiResponse(responseCode = "401", description = "Not authenticated"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    fun deleteCurrentUser(): ResponseEntity<Void> {
        val username = getCurrentUsername()
        val user = userService.findByUsername(username)
            ?: throw ResourceNotFoundException("User not found")

        userService.deleteById(user.id!!)
        return ResponseEntity.noContent().build()
    }

    private fun getCurrentUsername(): String {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw UnauthorizedException("Not authenticated")

        return authentication.name
            ?: throw UnauthorizedException("Authentication principal not found")
    }
}
