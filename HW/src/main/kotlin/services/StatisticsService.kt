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
    private val menu: Menu
) {

    fun leaveReview(
        username: String, orderId: Int, dish: Dish,
        score: Int, text: String,
    ): String {

        val order: Order
        try {
            order = orderRepository.checkUserAccessToOrder(username, orderId)
        } catch (e: NoSuchElementException) {
            return "Trying to leave review... FAIL\n" +
                    "ERROR: ${e.message}"
        } catch (e: AccessDeniedException) {
            return "Trying to leave review... FAIL\n" +
                    "ERROR: ${e.message}"
        }
        if(order.status != OrderStatus.PAID) {
            return "Trying to leave review... FAIL\n" +
                    "ERROR: Can't rate a not paid order!"
        }
        if (!order.listOfDishes.contains(dish)) {
            return "Trying to leave review... FAIL\n" +
                    "ERROR: Can't rate this dish!"
        }
        statsRepository.rateDish(dish, score, text)
        return "Trying to leave review... SUCCESS"
    }

    fun increaseRevenue(money: Int) {
        statsRepository.increaseRevenue(money)
    }

    fun getTopFiveDishes() : List<Dish> {
        return menu.getTopDishes()
    }

    fun getAverageRating() : Double {
        return menu.getAverageRating()
    }

    fun getAllReviewsForDish(dishName: String) : List<String> {
        return menu.getAllReviews(dishName)
    }

    fun showMenu() : String {
        return menu.toString()
    }

    fun showOrders() : String {
        return orderRepository.toString()
    }
}