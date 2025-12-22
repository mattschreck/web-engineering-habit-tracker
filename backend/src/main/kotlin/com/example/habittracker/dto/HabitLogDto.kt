package com.example.habittracker.dto

import com.example.habittracker.entity.HabitLog
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime

data class HabitLogRequest(
    @field:NotNull(message = "Habit ID is required")
    val habitId: Long,

    @field:NotNull(message = "Log date is required")
    val logDate: LocalDate,

    val completed: Boolean = true,

    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    val notes: String? = null
)

data class HabitLogResponse(
    val id: Long,
    val habitId: Long,
    val habitName: String,
    val logDate: LocalDate,
    val completed: Boolean,
    val notes: String?,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun fromEntity(habitLog: HabitLog): HabitLogResponse {
            return HabitLogResponse(
                id = habitLog.id!!,
                habitId = habitLog.habit.id!!,
                habitName = habitLog.habit.name,
                logDate = habitLog.logDate,
                completed = habitLog.completed,
                notes = habitLog.notes,
                createdAt = habitLog.createdAt
            )
        }
    }
}
