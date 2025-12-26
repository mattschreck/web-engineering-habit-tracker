package com.example.habittracker.controller

import com.example.habittracker.dto.HabitRequest
import com.example.habittracker.dto.HabitResponse
import com.example.habittracker.entity.Habit
import com.example.habittracker.exception.BadRequestException
import com.example.habittracker.exception.ResourceNotFoundException
import com.example.habittracker.service.HabitService
import com.example.habittracker.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/habits")
@Tag(name = "Habits", description = "CRUD operations for habits")
class HabitController(
    private val habitService: HabitService,
    private val userService: UserService
) {

    @GetMapping
    @Operation(summary = "Get all habits", description = "Retrieves all habits for a specific user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved habits"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    fun getAllHabits(@RequestParam userId: Long): ResponseEntity<List<HabitResponse>> {
        userService.findById(userId)
            ?: throw ResourceNotFoundException("User with id $userId not found")

        val habits = habitService.findByUserId(userId)
        val response = habits.map { HabitResponse.fromEntity(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/active")
    @Operation(summary = "Get active habits", description = "Retrieves all active habits for a specific user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved active habits"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    fun getActiveHabits(@RequestParam userId: Long): ResponseEntity<List<HabitResponse>> {
        userService.findById(userId)
            ?: throw ResourceNotFoundException("User with id $userId not found")

        val habits = habitService.findActiveByUserId(userId)
        val response = habits.map { HabitResponse.fromEntity(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get habit by ID", description = "Retrieves a specific habit by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved habit"),
            ApiResponse(responseCode = "404", description = "Habit not found")
        ]
    )
    fun getHabitById(@PathVariable id: Long): ResponseEntity<HabitResponse> {
        val habit = habitService.findById(id)
            ?: throw ResourceNotFoundException("Habit with id $id not found")

        return ResponseEntity.ok(HabitResponse.fromEntity(habit))
    }

    @PostMapping
    @Operation(summary = "Create a new habit", description = "Creates a new habit for a user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Habit successfully created"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    fun createHabit(
        @RequestParam userId: Long,
        @Valid @RequestBody request: HabitRequest
    ): ResponseEntity<HabitResponse> {
        val user = userService.findById(userId)
            ?: throw ResourceNotFoundException("User with id $userId not found")

        val habit = Habit(
            name = request.name,
            description = request.description,
            active = request.active,
            frequency = request.frequency,
            startDate = request.startDate,
            endDate = request.endDate,
            user = user
        )

        val savedHabit = habitService.save(habit)
        return ResponseEntity.status(HttpStatus.CREATED).body(HabitResponse.fromEntity(savedHabit))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a habit", description = "Updates an existing habit")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Habit successfully updated"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "404", description = "Habit not found")
        ]
    )
    fun updateHabit(
        @PathVariable id: Long,
        @Valid @RequestBody request: HabitRequest
    ): ResponseEntity<HabitResponse> {
        val existingHabit = habitService.findById(id)
            ?: throw ResourceNotFoundException("Habit with id $id not found")

        val updatedHabit = existingHabit.copy(
            name = request.name,
            description = request.description,
            active = request.active,
            frequency = request.frequency,
            startDate = request.startDate,
            endDate = request.endDate
        )

        val savedHabit = habitService.update(id, updatedHabit)
            ?: throw ResourceNotFoundException("Failed to update habit with id $id")

        return ResponseEntity.ok(HabitResponse.fromEntity(savedHabit))
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle habit active status", description = "Toggles the active status of a habit")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Habit status successfully toggled"),
            ApiResponse(responseCode = "404", description = "Habit not found")
        ]
    )
    fun toggleHabitActive(@PathVariable id: Long): ResponseEntity<HabitResponse> {
        val habit = habitService.toggleActive(id)
            ?: throw ResourceNotFoundException("Habit with id $id not found")

        return ResponseEntity.ok(HabitResponse.fromEntity(habit))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a habit", description = "Deletes a habit by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Habit successfully deleted"),
            ApiResponse(responseCode = "404", description = "Habit not found")
        ]
    )
    fun deleteHabit(@PathVariable id: Long): ResponseEntity<Void> {
        if (habitService.findById(id) == null) {
            throw ResourceNotFoundException("Habit with id $id not found")
        }

        habitService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
