package com.example.habittracker.service

import com.example.habittracker.entity.Habit
import com.example.habittracker.entity.HabitFrequency
import com.example.habittracker.entity.HabitLog
import com.example.habittracker.entity.User
import com.example.habittracker.repository.HabitLogRepository
import com.example.habittracker.repository.HabitRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class HabitLogServiceTest {

    @Mock
    private lateinit var habitLogRepository: HabitLogRepository

    @Mock
    private lateinit var habitRepository: HabitRepository

    @InjectMocks
    private lateinit var habitLogService: HabitLogService

    private lateinit var testUser: User
    private lateinit var testHabit: Habit
    private lateinit var testLog: HabitLog

    @BeforeEach
    fun setup() {
        testUser = User(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )

        testHabit = Habit(
            id = 1L,
            name = "Exercise",
            description = "Daily exercise routine",
            active = true,
            frequency = HabitFrequency.DAILY,
            user = testUser
        )

        testLog = HabitLog(
            id = 1L,
            habit = testHabit,
            logDate = LocalDate.now(),
            completed = true,
            notes = "Completed successfully"
        )
    }

    @Test
    fun `findById should return log when log exists`() {
        `when`(habitLogRepository.findById(1L)).thenReturn(Optional.of(testLog))

        val result = habitLogService.findById(1L)

        assertNotNull(result)
        assertEquals(testLog.id, result?.id)
        verify(habitLogRepository, times(1)).findById(1L)
    }

    @Test
    fun `findByHabitId should return list of logs`() {
        val logs = listOf(testLog, testLog.copy(id = 2L, logDate = LocalDate.now().minusDays(1)))
        `when`(habitLogRepository.findByHabitId(1L)).thenReturn(logs)

        val result = habitLogService.findByHabitId(1L)

        assertEquals(2, result.size)
        verify(habitLogRepository, times(1)).findByHabitId(1L)
    }

    @Test
    fun `findByHabitIdAndDate should return log when it exists`() {
        val date = LocalDate.now()
        `when`(habitLogRepository.findByHabitIdAndLogDate(1L, date)).thenReturn(Optional.of(testLog))

        val result = habitLogService.findByHabitIdAndDate(1L, date)

        assertNotNull(result)
        assertEquals(testLog.logDate, result?.logDate)
        verify(habitLogRepository, times(1)).findByHabitIdAndLogDate(1L, date)
    }

    @Test
    fun `findByHabitIdAndDateRange should return logs in range`() {
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        val logs = listOf(testLog)
        `when`(habitLogRepository.findByHabitIdAndLogDateBetween(1L, startDate, endDate)).thenReturn(logs)

        val result = habitLogService.findByHabitIdAndDateRange(1L, startDate, endDate)

        assertEquals(1, result.size)
        verify(habitLogRepository, times(1)).findByHabitIdAndLogDateBetween(1L, startDate, endDate)
    }

    @Test
    fun `findByUserIdAndDateRange should return user logs in range`() {
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        val logs = listOf(testLog)
        `when`(habitLogRepository.findByUserIdAndDateRange(1L, startDate, endDate)).thenReturn(logs)

        val result = habitLogService.findByUserIdAndDateRange(1L, startDate, endDate)

        assertEquals(1, result.size)
        verify(habitLogRepository, times(1)).findByUserIdAndDateRange(1L, startDate, endDate)
    }

    @Test
    fun `save should return saved log`() {
        val newLog = testLog.copy(id = null)
        `when`(habitLogRepository.save(newLog)).thenReturn(testLog)

        val result = habitLogService.save(newLog)

        assertNotNull(result)
        assertEquals(testLog.id, result.id)
        verify(habitLogRepository, times(1)).save(newLog)
    }

    @Test
    fun `logHabit should create new log when habit exists and no log for date`() {
        val date = LocalDate.now()
        `when`(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit))
        `when`(habitLogRepository.existsByHabitIdAndLogDate(1L, date)).thenReturn(false)
        `when`(habitLogRepository.save(any(HabitLog::class.java))).thenReturn(testLog)

        val result = habitLogService.logHabit(1L, date, "Great workout")

        assertNotNull(result)
        assertEquals(testLog.id, result?.id)
        verify(habitRepository, times(1)).findById(1L)
        verify(habitLogRepository, times(1)).existsByHabitIdAndLogDate(1L, date)
        verify(habitLogRepository, times(1)).save(any(HabitLog::class.java))
    }

    @Test
    fun `logHabit should return null when habit does not exist`() {
        val date = LocalDate.now()
        `when`(habitRepository.findById(999L)).thenReturn(Optional.empty())

        val result = habitLogService.logHabit(999L, date)

        assertNull(result)
        verify(habitRepository, times(1)).findById(999L)
        verify(habitLogRepository, never()).save(any(HabitLog::class.java))
    }

    @Test
    fun `logHabit should return null when log already exists for date`() {
        val date = LocalDate.now()
        `when`(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit))
        `when`(habitLogRepository.existsByHabitIdAndLogDate(1L, date)).thenReturn(true)

        val result = habitLogService.logHabit(1L, date)

        assertNull(result)
        verify(habitRepository, times(1)).findById(1L)
        verify(habitLogRepository, times(1)).existsByHabitIdAndLogDate(1L, date)
        verify(habitLogRepository, never()).save(any(HabitLog::class.java))
    }

    @Test
    fun `update should return updated log when log exists`() {
        val updatedLog = testLog.copy(notes = "Updated notes")
        `when`(habitLogRepository.existsById(1L)).thenReturn(true)
        `when`(habitLogRepository.save(any(HabitLog::class.java))).thenReturn(updatedLog)

        val result = habitLogService.update(1L, updatedLog)

        assertNotNull(result)
        assertEquals("Updated notes", result?.notes)
        verify(habitLogRepository, times(1)).existsById(1L)
        verify(habitLogRepository, times(1)).save(any(HabitLog::class.java))
    }

    @Test
    fun `deleteById should call repository deleteById`() {
        doNothing().`when`(habitLogRepository).deleteById(1L)

        habitLogService.deleteById(1L)

        verify(habitLogRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `findAll should return list of logs`() {
        val logs = listOf(testLog, testLog.copy(id = 2L))
        `when`(habitLogRepository.findAll()).thenReturn(logs)

        val result = habitLogService.findAll()

        assertEquals(2, result.size)
        verify(habitLogRepository, times(1)).findAll()
    }
}
