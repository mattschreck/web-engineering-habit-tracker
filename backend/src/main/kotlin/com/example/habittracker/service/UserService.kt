package com.example.habittracker.service

import com.example.habittracker.dto.UpdateUserRequest
import com.example.habittracker.entity.User
import com.example.habittracker.exception.ResourceAlreadyExistsException
import com.example.habittracker.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository
) {

    fun findAll(): List<User> = userRepository.findAll()

    fun findById(id: Long): User? = userRepository.findById(id).orElse(null)

    fun findByUsername(username: String): User? = userRepository.findByUsername(username).orElse(null)

    fun findByEmail(email: String): User? = userRepository.findByEmail(email).orElse(null)

    fun existsByUsername(username: String): Boolean = userRepository.existsByUsername(username)

    fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    fun save(user: User): User = userRepository.save(user)

    fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }

    fun update(id: Long, updatedUser: User): User? {
        return if (userRepository.existsById(id)) {
            userRepository.save(updatedUser.copy(id = id))
        } else {
            null
        }
    }

    fun updateCurrentUser(currentUser: User, request: UpdateUserRequest): User {
        var updated = currentUser

        request.username?.let { newUsername ->
            if (newUsername != currentUser.username) {
                val existingUser = userRepository.findByUsername(newUsername).orElse(null)
                if (existingUser != null && existingUser.id != currentUser.id) {
                    throw ResourceAlreadyExistsException("Username '$newUsername' is already taken")
                }
                updated = updated.copy(username = newUsername)
            }
        }

        return userRepository.save(updated)
    }
}
