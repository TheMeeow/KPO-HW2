package services

import dataComponents.User
import repositories.UserRepository

class UserService(private val userRepository: UserRepository) {

    fun tryRegisterUser(user: User) : String {
        try {
            userRepository.addUser(user)
        } catch (e: Exception) {
            return "Registration... FAIL\n" +
                    "ERROR: " + e.message
        }
        return "Registration... SUCCESS"
    }

    fun tryAuthenticateUser(username: String, password: String) : String {
        val user: User
        try {
            user = userRepository.getUserByUsername(username)
        } catch (e: NoSuchElementException) {
            return "Authentication... FAIL\n" +
                    "ERROR: " + e.message
        }

        val passwordHash = password.hashCode()
        if(user.passwordHash != passwordHash){
            return "Authentication... FAIL\n" +
                    "ERROR: Incorrect password!"
        }
        return "Authentication... SUCCESS"
    }

    fun tryDeleteUser(user: User) : String {
        try {
            userRepository.deleteUserByUsername(user.username, user)
        } catch (e: NoSuchElementException) {
            return "Trying to delete user... FAIL\n"+
                    "ERROR: " + e.message
        } catch (e: AccessDeniedException) {
            return "Trying to delete user... FAIL\n"+
                    "ERROR: " + e.message
        }
        return "Trying to delete user... SUCCESS"
    }
}