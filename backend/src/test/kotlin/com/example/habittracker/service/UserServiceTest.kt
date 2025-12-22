package com.example.habittracker.service

import com.example.habittracker.entity.User
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
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        testUser = User(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )
    }

    @Test
    fun `findById should return user when user exists`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(testUser))

        val result = userService.findById(1L)

        assertNotNull(result)
        assertEquals(testUser.username, result?.username)
        assertEquals(testUser.email, result?.email)
        verify(userRepository, times(1)).findById(1L)
    }

    @Test
    fun `findById should return null when user does not exist`() {
        `when`(userRepository.findById(999L)).thenReturn(Optional.empty())

        val result = userService.findById(999L)

        assertNull(result)
        verify(userRepository, times(1)).findById(999L)
    }

    @Test
    fun `findByUsername should return user when username exists`() {
        `when`(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser))

        val result = userService.findByUsername("testuser")

        assertNotNull(result)
        assertEquals(testUser.username, result?.username)
        verify(userRepository, times(1)).findByUsername("testuser")
    }

    @Test
    fun `findByEmail should return user when email exists`() {
        `when`(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser))

        val result = userService.findByEmail("test@example.com")

        assertNotNull(result)
        assertEquals(testUser.email, result?.email)
        verify(userRepository, times(1)).findByEmail("test@example.com")
    }

    @Test
    fun `existsByUsername should return true when username exists`() {
        `when`(userRepository.existsByUsername("testuser")).thenReturn(true)

        val result = userService.existsByUsername("testuser")

        assertTrue(result)
        verify(userRepository, times(1)).existsByUsername("testuser")
    }

    @Test
    fun `existsByEmail should return true when email exists`() {
        `when`(userRepository.existsByEmail("test@example.com")).thenReturn(true)

        val result = userService.existsByEmail("test@example.com")

        assertTrue(result)
        verify(userRepository, times(1)).existsByEmail("test@example.com")
    }

    @Test
    fun `save should return saved user`() {
        val newUser = testUser.copy(id = null)
        `when`(userRepository.save(newUser)).thenReturn(testUser)

        val result = userService.save(newUser)

        assertNotNull(result)
        assertEquals(testUser.id, result.id)
        assertEquals(testUser.username, result.username)
        verify(userRepository, times(1)).save(newUser)
    }

    @Test
    fun `update should return updated user when user exists`() {
        val updatedUser = testUser.copy(email = "updated@example.com")
        `when`(userRepository.existsById(1L)).thenReturn(true)
        `when`(userRepository.save(any(User::class.java))).thenReturn(updatedUser)

        val result = userService.update(1L, updatedUser)

        assertNotNull(result)
        assertEquals("updated@example.com", result?.email)
        verify(userRepository, times(1)).existsById(1L)
        verify(userRepository, times(1)).save(any(User::class.java))
    }

    @Test
    fun `update should return null when user does not exist`() {
        `when`(userRepository.existsById(999L)).thenReturn(false)

        val result = userService.update(999L, testUser)

        assertNull(result)
        verify(userRepository, times(1)).existsById(999L)
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `deleteById should call repository deleteById`() {
        doNothing().`when`(userRepository).deleteById(1L)

        userService.deleteById(1L)

        verify(userRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `findAll should return list of users`() {
        val users = listOf(testUser, testUser.copy(id = 2L, username = "user2", email = "user2@example.com"))
        `when`(userRepository.findAll()).thenReturn(users)

        val result = userService.findAll()

        assertEquals(2, result.size)
        verify(userRepository, times(1)).findAll()
    }
}
