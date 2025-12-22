package com.example.habittracker.controller

import com.example.habittracker.dto.HabitLogRequest
import com.example.habittracker.dto.HabitLogResponse
import com.example.habittracker.entity.HabitLog
import com.example.habittracker.exception.BadRequestException
import com.example.habittracker.exception.ResourceAlreadyExistsException
import com.example.habittracker.exception.ResourceNotFoundException
import com.example.habittracker.service.HabitLogService
import com.example.habittracker.service.HabitService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/habit-logs")
@Tag(name = "Habit Logs", description = "Tracking operations for habit logs")
class HabitLogController(
    private val habitLogService: HabitLogService,
    private val habitService: HabitService
) {

    @GetMapping
    @Operation(summary = "Get all habit logs", description = "Retrieves all habit logs for a specific habit")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved habit logs"),
            ApiResponse(responseCode = "404", description = "Habit not found")
        ]
    )
    fun getAllLogsForHabit(@RequestParam habitId: Long): ResponseEntity<List<HabitLogResponse>> {
        if (habitService.findById(habitId) == null) {
            throw ResourceNotFoundException("Habit with id $habitId not found")
        }

        val logs = habitLogService.findByHabitId(habitId)
        val response = logs.map { HabitLogResponse.fromEntity(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/range")
    @Operation(summary = "Get habit logs by date range", description = "Retrieves habit logs for a specific habit within a date range")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved habit logs"),
            ApiResponse(responseCode = "400", description = "Invalid date range"),
            ApiResponse(responseCode = "404", description = "Habit not found")
        ]
    )
    fun getLogsByDateRange(
        @RequestParam habitId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<List<HabitLogResponse>> {
        if (habitService.findById(habitId) == null) {
            throw ResourceNotFoundException("Habit with id $habitId not found")
        }

        if (startDate.isAfter(endDate)) {
            throw BadRequestException("Start date must be before or equal to end date")
        }

        val logs = habitLogService.findByHabitIdAndDateRange(habitId, startDate, endDate)
        val response = logs.map { HabitLogResponse.fromEntity(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/user")
    @Operation(summary = "Get all logs for user", description = "Retrieves all habit logs for a specific user within a date range")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved habit logs"),
            ApiResponse(responseCode = "400", description = "Invalid date range")
        ]
    )
    fun getLogsByUser(
        @RequestParam userId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<List<HabitLogResponse>> {
        if (startDate.isAfter(endDate)) {
            throw BadRequestException("Start date must be before or equal to end date")
        }

        val logs = habitLogService.findByUserIdAndDateRange(userId, startDate, endDate)
        val response = logs.map { HabitLogResponse.fromEntity(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get habit log by ID", description = "Retrieves a specific habit log by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved habit log"),
            ApiResponse(responseCode = "404", description = "Habit log not found")
        ]
    )
    fun getLogById(@PathVariable id: Long): ResponseEntity<HabitLogResponse> {
        val log = habitLogService.findById(id)
            ?: throw ResourceNotFoundException("Habit log with id $id not found")

        return ResponseEntity.ok(HabitLogResponse.fromEntity(log))
    }

    @PostMapping
    @Operation(summary = "Log a habit", description = "Creates a new habit log entry")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Habit log successfully created"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "404", description = "Habit not found"),
            ApiResponse(responseCode = "409", description = "Habit already logged for this date")
        ]
    )
    fun logHabit(@Valid @RequestBody request: HabitLogRequest): ResponseEntity<HabitLogResponse> {
        val habit = habitService.findById(request.habitId)
            ?: throw ResourceNotFoundException("Habit with id ${request.habitId} not found")

        val existingLog = habitLogService.findByHabitIdAndDate(request.habitId, request.logDate)
        if (existingLog != null) {
            throw ResourceAlreadyExistsException("Habit already logged for date ${request.logDate}")
        }

        val log = HabitLog(
            habit = habit,
            logDate = request.logDate,
            completed = request.completed,
            notes = request.notes
        )

        val savedLog = habitLogService.save(log)
        return ResponseEntity.status(HttpStatus.CREATED).body(HabitLogResponse.fromEntity(savedLog))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a habit log", description = "Updates an existing habit log")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Habit log successfully updated"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "404", description = "Habit log not found")
        ]
    )
    fun updateLog(
        @PathVariable id: Long,
        @Valid @RequestBody request: HabitLogRequest
    ): ResponseEntity<HabitLogResponse> {
        val existingLog = habitLogService.findById(id)
            ?: throw ResourceNotFoundException("Habit log with id $id not found")

        val habit = habitService.findById(request.habitId)
            ?: throw ResourceNotFoundException("Habit with id ${request.habitId} not found")

        val updatedLog = existingLog.copy(
            habit = habit,
            logDate = request.logDate,
            completed = request.completed,
            notes = request.notes
        )

        val savedLog = habitLogService.update(id, updatedLog)
            ?: throw ResourceNotFoundException("Failed to update habit log with id $id")

        return ResponseEntity.ok(HabitLogResponse.fromEntity(savedLog))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a habit log", description = "Deletes a habit log by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Habit log successfully deleted"),
            ApiResponse(responseCode = "404", description = "Habit log not found")
        ]
    )
    fun deleteLog(@PathVariable id: Long): ResponseEntity<Void> {
        if (habitLogService.findById(id) == null) {
            throw ResourceNotFoundException("Habit log with id $id not found")
        }

        habitLogService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
