package cs.hse.hw.repositories

import cs.hse.hw.serializationModule.Serializer
import cs.hse.hw.dataComponents.User
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val pathForSerialization: String = "users.json") {
    private var users: MutableList<User> = mutableListOf()
    private val serializer: Serializer = Serializer(pathForSerialization)
    init {
       users = deserializeUsers()
    }

    private fun deserializeUsers(): MutableList<User> {
        return serializer.deserializeListFromFile<User>().toMutableList()
    }

    fun serialiseUsers() {
        serializer.serializeListToFile(users)
    }

    fun addUser(newUser: User) {
        if (users.any { it.username == newUser.username }) {
            throw Exception("User with this username already exists!")
        }
        users.add(newUser)
    }

    fun deleteUser(userToDelete: User) {
        users.remove(userToDelete)
    }

    fun getUserByUsername(username: String): User {
        return users.find { it.username == username }
            ?: throw NoSuchElementException("No user with this username!")
    }

}