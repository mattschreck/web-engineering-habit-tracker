package com.example.habittracker.controller

import com.example.habittracker.dto.*
import com.example.habittracker.entity.User
import com.example.habittracker.exception.BadRequestException
import com.example.habittracker.exception.ResourceAlreadyExistsException
import com.example.habittracker.exception.UnauthorizedException
import com.example.habittracker.security.JwtTokenProvider
import com.example.habittracker.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for user registration and login")
class AuthController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided credentials")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User successfully registered"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "409", description = "User already exists")
        ]
    )
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        if (userService.existsByUsername(request.username)) {
            throw ResourceAlreadyExistsException("Username '${request.username}' is already taken")
        }

        if (userService.existsByEmail(request.email)) {
            throw ResourceAlreadyExistsException("Email '${request.email}' is already registered")
        }

        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            enabled = true
        )

        val savedUser = userService.save(user)
        val token = jwtTokenProvider.generateTokenFromUsername(savedUser.username)

        val response = AuthResponse(
            token = token,
            user = UserResponse.fromEntity(savedUser),
            message = "User registered successfully"
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user with username/email and password")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "401", description = "Invalid credentials")
        ]
    )
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val user = userService.findByUsername(request.usernameOrEmail)
            ?: userService.findByEmail(request.usernameOrEmail)
            ?: throw UnauthorizedException("Invalid username/email or password")

        if (!user.enabled) {
            throw UnauthorizedException("Account is disabled")
        }

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                user.username,
                request.password
            )
        )

        val token = jwtTokenProvider.generateToken(authentication)

        val response = AuthResponse(
            token = token,
            user = UserResponse.fromEntity(user),
            message = "Login successful"
        )

        return ResponseEntity.ok(response)
    }
}
