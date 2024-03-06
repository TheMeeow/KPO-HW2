package services

import dataComponents.Dish
import dataComponents.Order
import dataComponents.OrderStatus
import repositories.Menu
import repositories.OrderRepository
import repositories.StatisticsRepository

class StatisticsService(
    private val orderRepository: OrderRepository,
    private val statsRepository: StatisticsRepository,
    private val menu: Menu,
) {

    fun tryLeaveReview(
        username: String, orderId: Int, dish: Dish,
        score: Int, text: String,
    ): String {
        val output = "Trying to leave a review... "
        val order: Order
        try {
            order = orderRepository.checkUserAccessToOrder(username, orderId)
        } catch (e: NoSuchElementException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        } catch (e: AccessDeniedException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        }
        if (order.status != OrderStatus.PAID) {
            return output + "FAIL\n" +
                    "ERROR: Can't rate a not paid order!"
        }
        if (!order.listOfDishes.contains(dish)) {
            return output + "FAIL\n" +
                    "ERROR: Can't rate this dish!"
        }
        try {
            statsRepository.rateDish(dish, score, text)
        } catch (e: IllegalArgumentException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        }
        return output + "SUCCESS"
    }

    fun increaseRevenue(money: Int) {
        statsRepository.increaseRevenue(money)
    }

    fun getTopFiveDishes(): List<Dish> {
        return menu.getTopDishes()
    }

    fun getAverageRating(): Double {
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

    fun showMenu(): String {
        return menu.toString()
    }

    fun showOrders(): String {
        return orderRepository.toString()
    }
}