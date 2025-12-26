package com.example.habittracker.controller

import com.example.habittracker.dto.UserResponse
import com.example.habittracker.exception.ResourceNotFoundException
import com.example.habittracker.exception.UnauthorizedException
import com.example.habittracker.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
class UserController(
    private val userService: UserService
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
