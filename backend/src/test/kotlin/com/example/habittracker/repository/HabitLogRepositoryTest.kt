package com.example.habittracker.repository

import com.example.habittracker.entity.Habit
import com.example.habittracker.entity.HabitFrequency
import com.example.habittracker.entity.HabitLog
import com.example.habittracker.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
class HabitLogRepositoryTest {

    @Autowired
    private lateinit var habitLogRepository: HabitLogRepository

    @Autowired
    private lateinit var habitRepository: HabitRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: User
    private lateinit var testHabit: Habit

    @BeforeEach
    fun setup() {
        testUser = User(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )
        testUser = userRepository.save(testUser)

        testHabit = Habit(
            name = "Exercise",
            description = "Daily exercise routine",
            active = true,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )
        testHabit = habitRepository.save(testHabit)
    }

    @Test
    fun `should save and find habit log by id`() {
        val log = HabitLog(
            habit = testHabit,
            logDate = LocalDate.now(),
            completed = true,
            notes = "Completed successfully"
        )

        val savedLog = habitLogRepository.save(log)

        assertNotNull(savedLog.id)
        val foundLog = habitLogRepository.findById(savedLog.id!!).orElse(null)
        assertNotNull(foundLog)
        assertEquals(LocalDate.now(), foundLog.logDate)
    }

    @Test
    fun `should find logs by habit id`() {
        val log1 = HabitLog(
            habit = testHabit,
            logDate = LocalDate.now(),
            completed = true,
            notes = "Day 1"
        )
        val log2 = HabitLog(
            habit = testHabit,
            logDate = LocalDate.now().minusDays(1),
            completed = true,
            notes = "Day 2"
        )

        habitLogRepository.save(log1)
        habitLogRepository.save(log2)

        val logs = habitLogRepository.findByHabitId(testHabit.id!!)

        assertEquals(2, logs.size)
    }

    @Test
    fun `should find log by habit id and date`() {
        val date = LocalDate.now()
        val log = HabitLog(
            habit = testHabit,
            logDate = date,
            completed = true,
            notes = "Completed"
        )

        habitLogRepository.save(log)

        val foundLog = habitLogRepository.findByHabitIdAndLogDate(testHabit.id!!, date).orElse(null)

        assertNotNull(foundLog)
        assertEquals(date, foundLog.logDate)
    }

    @Test
    fun `should find logs by habit id and date range`() {
        val today = LocalDate.now()
        val log1 = HabitLog(
            habit = testHabit,
            logDate = today,
            completed = true
        )
        val log2 = HabitLog(
            habit = testHabit,
            logDate = today.minusDays(3),
            completed = true
        )
        val log3 = HabitLog(
            habit = testHabit,
            logDate = today.minusDays(10),
            completed = true
        )

        habitLogRepository.save(log1)
        habitLogRepository.save(log2)
        habitLogRepository.save(log3)

        val logs = habitLogRepository.findByHabitIdAndLogDateBetween(
            testHabit.id!!,
            today.minusDays(7),
            today
        )

        assertEquals(2, logs.size)
    }

    @Test
    fun `should find logs by user id and date range`() {
        val today = LocalDate.now()
        val log1 = HabitLog(
            habit = testHabit,
            logDate = today,
            completed = true
        )
        val log2 = HabitLog(
            habit = testHabit,
            logDate = today.minusDays(3),
            completed = true
        )

        habitLogRepository.save(log1)
        habitLogRepository.save(log2)

        val logs = habitLogRepository.findByUserIdAndDateRange(
            testUser.id!!,
            today.minusDays(7),
            today
        )

        assertEquals(2, logs.size)
    }

    @Test
    fun `should check if log exists by habit id and date`() {
        val date = LocalDate.now()
        val log = HabitLog(
            habit = testHabit,
            logDate = date,
            completed = true
        )

        habitLogRepository.save(log)

        val exists = habitLogRepository.existsByHabitIdAndLogDate(testHabit.id!!, date)

        assertTrue(exists)
    }

    @Test
    fun `should return false when log does not exist for date`() {
        val exists = habitLogRepository.existsByHabitIdAndLogDate(testHabit.id!!, LocalDate.now())

        assertFalse(exists)
    }

    @Test
    fun `should enforce unique constraint on habit id and log date`() {
        val date = LocalDate.now()
        val log1 = HabitLog(
            habit = testHabit,
            logDate = date,
            completed = true
        )
        val log2 = HabitLog(
            habit = testHabit,
            logDate = date,
            completed = true
        )

        habitLogRepository.save(log1)

        assertThrows(Exception::class.java) {
            habitLogRepository.saveAndFlush(log2)
        }
    }

    @Test
    fun `should find logs by habit`() {
        val log = HabitLog(
            habit = testHabit,
            logDate = LocalDate.now(),
            completed = true
        )
        habitLogRepository.save(log)

        val logs = habitLogRepository.findByHabit(testHabit)

        assertEquals(1, logs.size)
    }
}
