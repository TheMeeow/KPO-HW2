package cs.hse.hw.controllers

import cs.hse.hw.dataComponents.Dish
import cs.hse.hw.dataComponents.User
import cs.hse.hw.services.StatisticsService
import cs.hse.hw.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.nio.file.AccessDeniedException

@RestController
@RequestMapping("/stats")
class StatisticsController(private val statisticsService: StatisticsService,
        private val userService: UserService) {
private var response = ""

    @GetMapping()
    fun showStats(): ResponseEntity<String> {
        try {
            userService.tryGetAdminAccess()
            response = statisticsService.showStats()
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/showOrders")
    fun showOrders(): ResponseEntity<String> {
        try {
            userService.tryGetAdminAccess()
            response = statisticsService.showOrders()
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/leaveReview")
    fun leaveReview(@RequestParam orderId: Int, @RequestParam dishName: String,
                    @RequestParam score: Int, @RequestParam text: String): ResponseEntity<String> {
        if (text.contains("ERROR")) {
            return ResponseEntity.badRequest().body("Illegal word in your review!")
        }
        try {
            val user = userService.getCurrentUser()
            response = statisticsService.tryLeaveReview(user.username, orderId, dishName, score, text)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/getTopFiveDishes")
    fun getTopFiveDishes(): ResponseEntity<List<String>> {
        val response: MutableList<String> = mutableListOf()
        val dishes: List<Dish>
        try {
            userService.tryGetAdminAccess()
            dishes = statisticsService.getTopFiveDishes()
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(listOf(e.message!!))
        }
        for (dish in dishes) {
            response.add(dish.toString())
        }
        return ResponseEntity.ok(response.toList())
    }

    @GetMapping("/getAllReviewsForDish")
    fun getAllReviewsForDish(@RequestParam dishName: String): ResponseEntity<List<String>> {
        val response: List<String>
        try {
            userService.tryGetAdminAccess()
            response = statisticsService.getAllReviewsForDish(dishName)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(listOf(e.message!!))
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/saveCurrentStats")
    fun saveCurrentStats(): ResponseEntity<String> {
        try {
            userService.tryGetAdminAccess()
            response = statisticsService.serializeStats()
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }
}