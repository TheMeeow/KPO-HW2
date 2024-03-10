package cs.hse.hw.services

import cs.hse.hw.dataComponents.User
import cs.hse.hw.repositories.StatisticsRepository
import org.springframework.stereotype.Service
import cs.hse.hw.repositories.UserRepository
import java.nio.file.AccessDeniedException

@Service
class UserService(private val userRepository: UserRepository,
                  private val statsRepository: StatisticsRepository) {

    private var currentUser: User? = User("user", -891985903, false)

    fun getCurrentUser(): User = currentUser
            ?: throw NullPointerException("You should firstly log in!")

    fun tryRegisterUser(user: User): String {
        try {
            userRepository.addUser(user)
        } catch (e: Exception) {
            return "Registration... FAIL\n" +
                    "ERROR: " + e.message
        }
        statsRepository.addUser()
        return "Registration... SUCCESS"
    }

    fun tryAuthenticateUser(username: String, password: String): String {
        val user: User
        val output = "Authentication... "
        try {
            user = userRepository.getUserByUsername(username)
        } catch (e: NoSuchElementException) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
        }

        val passwordHash = password.hashCode()
        if (user.passwordHash != passwordHash) {
            return output + "FAIL\n" +
                    "ERROR: Incorrect password!"
        }
        currentUser = user
        return output + "SUCCESS"
    }

    fun tryDeleteUser(): String {
        try {
            getCurrentUser()
        } catch (e: NullPointerException) {
            return e.message!!
        }
        val output = "Trying to delete user... "
        try {
            userRepository.deleteUser(currentUser!!)
        } catch (e: Exception) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
        }
        currentUser = null
        statsRepository.subtractUser()
        return output + "SUCCESS"
    }

    fun tryGetAdminAccess() {
        val user = getCurrentUser()
        if(!user.isAdmin) {
            throw AccessDeniedException("You have no access!" +
                    "\nOnly a user with administrator rights can make this request")
        }
    }

    fun serializeUsers() {
        try {
            userRepository.serialiseUsers()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}