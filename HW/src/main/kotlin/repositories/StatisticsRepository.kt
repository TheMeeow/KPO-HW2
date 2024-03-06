package repositories

import dataComponents.Dish
import dataComponents.FuckingSerializer


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

    fun serialiseList() {
        val stats = mutableListOf(revenue, countOfUsers, countOfOrders, countOfPaidOrders)
        FuckingSerializer("no").serializeListToFile(stats, "lolStas.json")
    }

    fun deserializeList() {
        val stats = FuckingSerializer("no").deserializeListFromFile<Int>("lolStas.json")
        revenue = stats[0]
        countOfUsers = stats[1]
        countOfOrders = stats[2]
        countOfPaidOrders = stats[3]
    }


    fun rateDish(dish: Dish, score: Int, text: String) {
        dish.rating.addReview(score, text)
    }
}