package cs.hse.hw.services

import cs.hse.hw.dataComponents.Dish
import com.example.hw.dataComponents.Order
import com.example.hw.dataComponents.OrderStatus
import org.springframework.stereotype.Service
import cs.hse.hw.repositories.Menu
import cs.hse.hw.repositories.OrderRepository
import cs.hse.hw.repositories.StatisticsRepository
import kotlinx.serialization.SerializationException
import java.nio.file.AccessDeniedException

@Service
class StatisticsService(
        private val orderRepository: OrderRepository,
        private val statsRepository: StatisticsRepository,
        private val menu: Menu,
) {

    fun tryLeaveReview(
            username: String, orderId: Int, dishName: String,
            score: Int, text: String,
    ): String {
        val output = "Trying to leave a review... "
        val order: Order
        val dish: Dish
        try {
            order = orderRepository.checkUserAccessToOrder(username, orderId)
            dish = menu.getDishByName(dishName)
        } catch (e: Exception) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        }
        if (order.status != OrderStatus.PAID) {
            return output + "FAIL\n" +
                    "ERROR: Can't rate a not paid order!"
        }
        if (!order.listOfDishes.contains(dish)) {
            return output + "FAIL\n" +
                    "ERROR: Can't rate this dish! " +
                    "You may only rate the dishes from order $orderId"
        }
        try {
            statsRepository.rateDish(dish, score, text)
        } catch (e: IllegalArgumentException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        }
        return output + "SUCCESS"
    }

    fun getTopFiveDishes(): List<Dish> {
        return menu.getTopDishes()
    }

    private fun getAverageRating(): Double {
        return menu.getAverageRating()
    }

    fun getAllReviewsForDish(dishName: String): List<String> {
        return try {
            val dish = menu.getDishByName(dishName)
            menu.getAllReviews(dish)
        } catch (e: NoSuchElementException) {
            listOf("ERROR:" + e.message)
        }
    }

    fun showOrders(): String {
        return orderRepository.toString()
    }

    fun showStats(): String {
        var output = statsRepository.toString()
        output += "Average dishes rating = ${getAverageRating()}\n"
        return output
    }

    fun serializeStats(): String {
        try {
        statsRepository.serialiseStats()
        } catch (e: SerializationException) {
            return e.message!!
        }
        return "Serialization... SUCCESS"
    }
}