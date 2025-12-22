package com.example.habittracker.repository

import com.example.habittracker.entity.Habit
import com.example.habittracker.entity.HabitLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface HabitLogRepository : JpaRepository<HabitLog, Long> {
    fun findByHabit(habit: Habit): List<HabitLog>
    fun findByHabitId(habitId: Long): List<HabitLog>
    fun findByHabitIdAndLogDate(habitId: Long, logDate: LocalDate): Optional<HabitLog>
    fun findByHabitIdAndLogDateBetween(habitId: Long, startDate: LocalDate, endDate: LocalDate): List<HabitLog>

    @Query("SELECT hl FROM HabitLog hl WHERE hl.habit.user.id = :userId AND hl.logDate BETWEEN :startDate AND :endDate")
    fun findByUserIdAndDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): List<HabitLog>

    fun existsByHabitIdAndLogDate(habitId: Long, logDate: LocalDate): Boolean
}
