package com.example.habittracker.dto

import com.example.habittracker.entity.Habit
import com.example.habittracker.entity.HabitFrequency
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime

data class HabitRequest(
    @field:NotBlank(message = "Habit name is required")
    @field:Size(min = 1, max = 100, message = "Habit name must be between 1 and 100 characters")
    val name: String,

    @field:Size(max = 500, message = "Description cannot exceed 500 characters")
    val description: String? = null,

    val active: Boolean = true,

    val frequency: HabitFrequency = HabitFrequency.DAILY,

    val startDate: LocalDate? = null,

    val endDate: LocalDate? = null
)

data class HabitResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val active: Boolean,
    val frequency: HabitFrequency,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val userId: Long,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun fromEntity(habit: Habit): HabitResponse {
            return HabitResponse(
                id = habit.id!!,
                name = habit.name,
                description = habit.description,
                active = habit.active,
                frequency = habit.frequency,
                startDate = habit.startDate,
                endDate = habit.endDate,
                userId = habit.user.id!!,
                createdAt = habit.createdAt,
                updatedAt = habit.updatedAt
            )
        }
    }
}
