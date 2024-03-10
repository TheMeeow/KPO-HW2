package cs.hse.hw.dataComponents

import kotlinx.serialization.Serializable


@Serializable
class User(val username: String, val passwordHash: Int, val isAdmin: Boolean) {
}