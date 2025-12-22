package com.example.habittracker.service

import com.example.habittracker.entity.Habit
import com.example.habittracker.entity.HabitFrequency
import com.example.habittracker.entity.User
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class HabitServiceTest {

    @Mock
    private lateinit var habitRepository: HabitRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var habitService: HabitService

    private lateinit var testUser: User
    private lateinit var testHabit: Habit

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
    }

    @Test
    fun `findById should return habit when habit exists`() {
        `when`(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit))

        val result = habitService.findById(1L)

        assertNotNull(result)
        assertEquals(testHabit.name, result?.name)
        verify(habitRepository, times(1)).findById(1L)
    }

    @Test
    fun `findById should return null when habit does not exist`() {
        `when`(habitRepository.findById(999L)).thenReturn(Optional.empty())

        val result = habitService.findById(999L)

        assertNull(result)
        verify(habitRepository, times(1)).findById(999L)
    }

    @Test
    fun `findByUserId should return list of habits`() {
        val habits = listOf(testHabit, testHabit.copy(id = 2L, name = "Reading"))
        `when`(habitRepository.findByUserId(1L)).thenReturn(habits)

        val result = habitService.findByUserId(1L)

        assertEquals(2, result.size)
        verify(habitRepository, times(1)).findByUserId(1L)
    }

    @Test
    fun `findActiveByUserId should return only active habits`() {
        val activeHabits = listOf(testHabit)
        `when`(habitRepository.findByUserIdAndActive(1L, true)).thenReturn(activeHabits)

        val result = habitService.findActiveByUserId(1L)

        assertEquals(1, result.size)
        assertTrue(result[0].active)
        verify(habitRepository, times(1)).findByUserIdAndActive(1L, true)
    }

    @Test
    fun `save should return saved habit`() {
        val newHabit = testHabit.copy(id = null)
        `when`(habitRepository.save(newHabit)).thenReturn(testHabit)

        val result = habitService.save(newHabit)

        assertNotNull(result)
        assertEquals(testHabit.id, result.id)
        assertEquals(testHabit.name, result.name)
        verify(habitRepository, times(1)).save(newHabit)
    }

    @Test
    fun `update should return updated habit when habit exists`() {
        val updatedHabit = testHabit.copy(name = "Updated Exercise")
        `when`(habitRepository.existsById(1L)).thenReturn(true)
        `when`(habitRepository.save(any(Habit::class.java))).thenReturn(updatedHabit)

        val result = habitService.update(1L, updatedHabit)

        assertNotNull(result)
        assertEquals("Updated Exercise", result?.name)
        verify(habitRepository, times(1)).existsById(1L)
        verify(habitRepository, times(1)).save(any(Habit::class.java))
    }

    @Test
    fun `update should return null when habit does not exist`() {
        `when`(habitRepository.existsById(999L)).thenReturn(false)

        val result = habitService.update(999L, testHabit)

        assertNull(result)
        verify(habitRepository, times(1)).existsById(999L)
        verify(habitRepository, never()).save(any(Habit::class.java))
    }

    @Test
    fun `toggleActive should toggle habit status when habit exists`() {
        `when`(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit))
        `when`(habitRepository.save(any(Habit::class.java))).thenAnswer { invocation ->
            invocation.getArgument(0)
        }

        val result = habitService.toggleActive(1L)

        assertNotNull(result)
        assertFalse(result?.active ?: true)
        verify(habitRepository, times(1)).findById(1L)
        verify(habitRepository, times(1)).save(any(Habit::class.java))
    }

    @Test
    fun `toggleActive should return null when habit does not exist`() {
        `when`(habitRepository.findById(999L)).thenReturn(Optional.empty())

        val result = habitService.toggleActive(999L)

        assertNull(result)
        verify(habitRepository, times(1)).findById(999L)
        verify(habitRepository, never()).save(any(Habit::class.java))
    }

    @Test
    fun `deleteById should call repository deleteById`() {
        doNothing().`when`(habitRepository).deleteById(1L)

        habitService.deleteById(1L)

        verify(habitRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `findAll should return list of habits`() {
        val habits = listOf(testHabit, testHabit.copy(id = 2L, name = "Reading"))
        `when`(habitRepository.findAll()).thenReturn(habits)

        val result = habitService.findAll()

        assertEquals(2, result.size)
        verify(habitRepository, times(1)).findAll()
    }
}
