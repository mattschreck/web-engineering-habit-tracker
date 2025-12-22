package com.example.habittracker.service

import com.example.habittracker.entity.Habit
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class HabitService(
    private val habitRepository: HabitRepository,
    private val userRepository: UserRepository
) {

    fun findAll(): List<Habit> = habitRepository.findAll()

    fun findById(id: Long): Habit? = habitRepository.findById(id).orElse(null)

    fun findByUserId(userId: Long): List<Habit> = habitRepository.findByUserId(userId)

    fun findActiveByUserId(userId: Long): List<Habit> = habitRepository.findByUserIdAndActive(userId, true)

    fun save(habit: Habit): Habit = habitRepository.save(habit)

    fun deleteById(id: Long) {
        habitRepository.deleteById(id)
    }

    fun update(id: Long, updatedHabit: Habit): Habit? {
        return if (habitRepository.existsById(id)) {
            habitRepository.save(updatedHabit.copy(id = id))
        } else {
            null
        }
    }

    fun toggleActive(id: Long): Habit? {
        val habit = findById(id) ?: return null
        return habitRepository.save(habit.copy(active = !habit.active))
    }
}
