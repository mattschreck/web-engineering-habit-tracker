package com.example.habittracker.service

import com.example.habittracker.entity.HabitLog
import com.example.habittracker.repository.HabitLogRepository
import com.example.habittracker.repository.HabitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class HabitLogService(
    private val habitLogRepository: HabitLogRepository,
    private val habitRepository: HabitRepository
) {

    fun findAll(): List<HabitLog> = habitLogRepository.findAll()

    fun findById(id: Long): HabitLog? = habitLogRepository.findById(id).orElse(null)

    fun findByHabitId(habitId: Long): List<HabitLog> = habitLogRepository.findByHabitId(habitId)

    fun findByHabitIdAndDate(habitId: Long, date: LocalDate): HabitLog? =
        habitLogRepository.findByHabitIdAndLogDate(habitId, date).orElse(null)

    fun findByHabitIdAndDateRange(habitId: Long, startDate: LocalDate, endDate: LocalDate): List<HabitLog> =
        habitLogRepository.findByHabitIdAndLogDateBetween(habitId, startDate, endDate)

    fun findByUserIdAndDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): List<HabitLog> =
        habitLogRepository.findByUserIdAndDateRange(userId, startDate, endDate)

    fun save(habitLog: HabitLog): HabitLog = habitLogRepository.save(habitLog)

    fun logHabit(habitId: Long, date: LocalDate, notes: String? = null): HabitLog? {
        val habit = habitRepository.findById(habitId).orElse(null) ?: return null

        if (habitLogRepository.existsByHabitIdAndLogDate(habitId, date)) {
            return null
        }

        val log = HabitLog(
            habit = habit,
            logDate = date,
            completed = true,
            notes = notes
        )
        return habitLogRepository.save(log)
    }

    fun deleteById(id: Long) {
        habitLogRepository.deleteById(id)
    }

    fun update(id: Long, updatedLog: HabitLog): HabitLog? {
        return if (habitLogRepository.existsById(id)) {
            habitLogRepository.save(updatedLog.copy(id = id))
        } else {
            null
        }
    }
}
