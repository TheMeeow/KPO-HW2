package cs.hse.hw.controllers

import cs.hse.hw.dataComponents.User
import cs.hse.hw.services.OrderService
import cs.hse.hw.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.nio.file.AccessDeniedException

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService,
                      private val userService: UserService) {
    private var response = ""

    @PostMapping("/makeOrder")
    fun makeOrder(@RequestBody dishes: List<String>): ResponseEntity<String> {
        try {
            val user = checkUserRights()
            response = orderService.tryMakeOrder(user.username, dishes)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/cancelOrder")
    fun cancelOrder(orderId: Int): ResponseEntity<String> {
        try {
            val user = checkUserRights()
            response = orderService.tryCancelOrder(user.username, orderId)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("ERROR: " + e.message) }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/addDishToOrder")
    fun addDishToOrder(orderId: Int, dishName: String): ResponseEntity<String> {
        try {
            val user = checkUserRights()
            response = orderService.tryAddDishToOrder(user.username, orderId, dishName)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("ERROR: " + e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/getOrderStatus")
    fun getOrderStatus(orderId: Int): ResponseEntity<String> {
        try {
            val user = userService.getCurrentUser()
            response = orderService.getOrderStatus(user, orderId)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("ERROR: " + e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/payForOrder")
    fun payForOrder(orderId: Int): ResponseEntity<String> {
        try {
            val user = checkUserRights()
            response = orderService.tryToPayForOrder(user.username, orderId)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("ERROR: " + e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    private fun checkUserRights(): User {
        val user = userService.getCurrentUser()
        if(user.isAdmin) {
            throw AccessDeniedException("You have no access!" +
                    "\nOnly regular users may carry out manipulations with orders")
        }
        return user
    }

}