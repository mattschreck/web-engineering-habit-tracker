package com.example.habittracker.repository

import com.example.habittracker.entity.Habit
import com.example.habittracker.entity.HabitFrequency
import com.example.habittracker.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class HabitRepositoryTest {

    @Autowired
    private lateinit var habitRepository: HabitRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        testUser = User(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )
        testUser = userRepository.save(testUser)
    }

    @Test
    fun `should save and find habit by id`() {
        val habit = Habit(
            name = "Exercise",
            description = "Daily exercise routine",
            active = true,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )

        val savedHabit = habitRepository.save(habit)

        assertNotNull(savedHabit.id)
        val foundHabit = habitRepository.findById(savedHabit.id!!).orElse(null)
        assertNotNull(foundHabit)
        assertEquals("Exercise", foundHabit.name)
    }

    @Test
    fun `should find habits by user id`() {
        val habit1 = Habit(
            name = "Exercise",
            description = "Daily exercise routine",
            active = true,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )
        val habit2 = Habit(
            name = "Reading",
            description = "Read books",
            active = true,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )

        habitRepository.save(habit1)
        habitRepository.save(habit2)

        val habits = habitRepository.findByUserId(testUser.id!!)

        assertEquals(2, habits.size)
    }

    @Test
    fun `should find only active habits by user id`() {
        val habit1 = Habit(
            name = "Exercise",
            description = "Daily exercise routine",
            active = true,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )
        val habit2 = Habit(
            name = "Reading",
            description = "Read books",
            active = false,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )

        habitRepository.save(habit1)
        habitRepository.save(habit2)

        val activeHabits = habitRepository.findByUserIdAndActive(testUser.id!!, true)

        assertEquals(1, activeHabits.size)
        assertEquals("Exercise", activeHabits[0].name)
    }

    @Test
    fun `should find habits by user`() {
        val habit = Habit(
            name = "Exercise",
            description = "Daily exercise routine",
            active = true,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )

        habitRepository.save(habit)

        val habits = habitRepository.findByUser(testUser)

        assertEquals(1, habits.size)
        assertEquals("Exercise", habits[0].name)
    }

    @Test
    fun `should find habits by user and active status`() {
        val habit1 = Habit(
            name = "Exercise",
            description = "Daily exercise routine",
            active = true,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )
        val habit2 = Habit(
            name = "Reading",
            description = "Read books",
            active = false,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )

        habitRepository.save(habit1)
        habitRepository.save(habit2)

        val activeHabits = habitRepository.findByUserAndActive(testUser, true)

        assertEquals(1, activeHabits.size)
        assertTrue(activeHabits[0].active)
    }

    @Test
    fun `should delete habit by id`() {
        val habit = Habit(
            name = "Exercise",
            description = "Daily exercise routine",
            active = true,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )
        val savedHabit = habitRepository.save(habit)

        habitRepository.deleteById(savedHabit.id!!)

        val foundHabit = habitRepository.findById(savedHabit.id!!).orElse(null)
        assertNull(foundHabit)
    }

}
