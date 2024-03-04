package repositories

import dataComponents.Dish

class StatisticsRepository {
    private var revenue: Int = 0
    private var countOfUsers: Int = 0
    private var countOfOrders: Int = 0
    private var countOfPaidOrders: Int = 0

    fun increaseRevenue(money: Int) {
        revenue += money
    }

    fun addUser() {
        countOfUsers++
    }

    fun subtractUser() {
        countOfUsers--
    }

    fun addOrder() {
        countOfOrders++
    }

    fun subtractOrder() {
        countOfOrders--
    }

    fun addPaidOrder() {
        countOfPaidOrders++
    }

    fun rateDish(dish: Dish, score: Int, text: String) {
        dish.rating.addReview(score, text)
    }
}