package com.example.habittracker.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "habit_logs",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["habit_id", "log_date"])
    ],
    indexes = [
        Index(name = "idx_habit_date", columnList = "habit_id, log_date")
    ]
)
data class HabitLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    val habit: Habit,

    @Column(nullable = false)
    val logDate: LocalDate,

    @Column(nullable = false)
    val completed: Boolean = true,

    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Column(length = 500)
    val notes: String? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null
)
