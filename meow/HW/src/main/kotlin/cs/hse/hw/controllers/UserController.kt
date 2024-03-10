package cs.hse.hw.controllers

import cs.hse.hw.dataComponents.User
import cs.hse.hw.dataComponents.UserData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import cs.hse.hw.services.UserService

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    private var response = ""

    @PostMapping("/register")
    fun register(@RequestBody user: UserData, @RequestParam isAdmin: Boolean): ResponseEntity<String> {
        val passwordHash = user.password.hashCode()
        response = userService.tryRegisterUser(User(user.username, passwordHash, isAdmin))
        if(response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        userService.serializeUsers()
        return ResponseEntity.ok("$response\nhash = $passwordHash")
    }

    @PostMapping("/deleteMyProfile")
    fun deleteUser(): ResponseEntity<String> {
        response = userService.tryDeleteUser()
        if(response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        userService.serializeUsers()
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun logIn(@RequestBody user: UserData): ResponseEntity<String> {
        response = userService.tryAuthenticateUser(user.username, user.password)
        if(response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

/*
    @PostMapping("/saveUsers")
    fun serializeUsers(): ResponseEntity<String> {
        userService.serializeUsers()
        return ResponseEntity.ok("Users were serialized")
    }
 */

}