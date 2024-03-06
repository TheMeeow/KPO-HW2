package repositories

import dataComponents.FuckingSerializer
import dataComponents.User
import java.nio.file.AccessDeniedException

class UserRepository(private var path: String = "users.json") {
    private var users: MutableList<User> = mutableListOf()

    init {
        users = deserializeUsers(path)
    }

    private fun deserializeUsers(pathToFile: String): MutableList<User> {
        return FuckingSerializer("a").deserializeListFromFile<User>("fuckingUsers.json").toMutableList()
    }

    fun addUser(newUser: User) {
        if (users.any { it.username == newUser.username }) {
            throw Exception("User with this username already exists!")
        }
        users.add(newUser)
    }

    fun deleteUserByUsername(username: String, requestSender: User) {
        if (username != requestSender.username) {
            if (!requestSender.isAdmin) {
                throw AccessDeniedException("You have no rights to delete this user!")
            }
        }
        val userToDelete = getUserByUsername(username)
        users.remove(userToDelete)
    }


    fun getUserByUsername(username: String): User {
        return users.find { it.username == username }
            ?: throw NoSuchElementException("No user with this username!")
    }

    fun countUsers() : Int {
        return users.size
    }

    fun serialiseUsers() {
        FuckingSerializer("f").serializeListToFile(users, "fuckingUsers.json")
    }
}