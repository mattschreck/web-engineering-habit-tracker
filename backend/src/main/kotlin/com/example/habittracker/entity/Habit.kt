package com.example.habittracker.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "habits")
data class Habit(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank(message = "Habit name is required")
    @field:Size(min = 1, max = 100, message = "Habit name must be between 1 and 100 characters")
    @Column(nullable = false, length = 100)
    val name: String,

    @field:Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    val description: String? = null,

    @Column(nullable = false)
    val active: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val frequency: HabitFrequency = HabitFrequency.DAILY,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(nullable = false)
    val updatedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "habit", cascade = [CascadeType.ALL], orphanRemoval = true)
    val logs: MutableList<HabitLog> = mutableListOf()
)

enum class HabitFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}
