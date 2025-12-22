package com.example.habittracker.repository

import com.example.habittracker.entity.Habit
import com.example.habittracker.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HabitRepository : JpaRepository<Habit, Long> {
    fun findByUser(user: User): List<Habit>
    fun findByUserId(userId: Long): List<Habit>
    fun findByUserIdAndActive(userId: Long, active: Boolean): List<Habit>
    fun findByUserAndActive(user: User, active: Boolean): List<Habit>
}
