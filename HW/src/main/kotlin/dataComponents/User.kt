package dataComponents

import kotlinx.serialization.Serializable

@Serializable
data class User(val username: String, val passwordHash: Int, val isAdmin: Boolean) {
}