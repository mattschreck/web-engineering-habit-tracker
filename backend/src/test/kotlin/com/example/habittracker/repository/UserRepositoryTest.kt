package com.example.habittracker.repository

import com.example.habittracker.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should save and find user by id`() {
        val user = User(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )

        val savedUser = userRepository.save(user)

        assertNotNull(savedUser.id)
        val foundUser = userRepository.findById(savedUser.id!!).orElse(null)
        assertNotNull(foundUser)
        assertEquals("testuser", foundUser.username)
        assertEquals("test@example.com", foundUser.email)
    }

    @Test
    fun `should find user by username`() {
        val user = User(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )
        userRepository.save(user)

        val foundUser = userRepository.findByUsername("testuser").orElse(null)

        assertNotNull(foundUser)
        assertEquals("testuser", foundUser.username)
    }

    @Test
    fun `should find user by email`() {
        val user = User(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )
        userRepository.save(user)

        val foundUser = userRepository.findByEmail("test@example.com").orElse(null)

        assertNotNull(foundUser)
        assertEquals("test@example.com", foundUser.email)
    }

    @Test
    fun `should return true when username exists`() {
        val user = User(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )
        userRepository.save(user)

        val exists = userRepository.existsByUsername("testuser")

        assertTrue(exists)
    }

    @Test
    fun `should return false when username does not exist`() {
        val exists = userRepository.existsByUsername("nonexistent")

        assertFalse(exists)
    }

    @Test
    fun `should return true when email exists`() {
        val user = User(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )
        userRepository.save(user)

        val exists = userRepository.existsByEmail("test@example.com")

        assertTrue(exists)
    }

    @Test
    fun `should return false when email does not exist`() {
        val exists = userRepository.existsByEmail("nonexistent@example.com")

        assertFalse(exists)
    }

    @Test
    fun `should delete user by id`() {
        val user = User(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )
        val savedUser = userRepository.save(user)

        userRepository.deleteById(savedUser.id!!)

        val foundUser = userRepository.findById(savedUser.id!!).orElse(null)
        assertNull(foundUser)
    }

    @Test
    fun `should enforce unique username constraint`() {
        val user1 = User(
            username = "testuser",
            email = "test1@example.com",
            password = "password123",
            enabled = true
        )
        userRepository.save(user1)

        val user2 = User(
            username = "testuser",
            email = "test2@example.com",
            password = "password123",
            enabled = true
        )

        assertThrows(Exception::class.java) {
            userRepository.saveAndFlush(user2)
        }
    }

    @Test
    fun `should enforce unique email constraint`() {
        val user1 = User(
            username = "testuser1",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )
        userRepository.save(user1)

        val user2 = User(
            username = "testuser2",
            email = "test@example.com",
            password = "password123",
            enabled = true
        )

        assertThrows(Exception::class.java) {
            userRepository.saveAndFlush(user2)
        }
    }
}
