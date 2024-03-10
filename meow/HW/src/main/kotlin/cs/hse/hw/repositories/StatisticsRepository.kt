package cs.hse.hw.repositories

import cs.hse.hw.dataComponents.Dish
import org.springframework.stereotype.Repository
import cs.hse.hw.serializationModule.Serializer
import kotlinx.serialization.SerializationException

@Repository
class StatisticsRepository(private val pathForSerialization: String = "stats.json") {
    private val serializer: Serializer = Serializer(pathForSerialization)

    private var revenue: Int = 0
    private var countOfUsers: Int = 0
    private var countOfOrders: Int = 0
    private var countOfPaidOrders: Int = 0

    init {
        deserializeStats()
    }

    fun serialiseStats() {
        val stats = mutableListOf(revenue, countOfUsers, countOfOrders, countOfPaidOrders)
        serializer.serializeListToFile(stats)
    }

    private fun deserializeStats() {
        val stats = serializer.deserializeListFromFile<Int>()
        if (stats.size == 4) {
            revenue = stats[0]
            countOfUsers = stats[1]
            countOfOrders = stats[2]
            countOfPaidOrders = stats[3]
        }
    }

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

    override fun toString(): String {
        var output = "-------------- Stats --------------\n"
        output += "Current revenue = ${revenue}\n"
        output += "Count of users in system = ${countOfUsers}\n"
        output += "Count of orders in system = ${countOfOrders}\n"
        output += "Count of paid orders = ${countOfPaidOrders}\n"
        output += "----------------------------------\n"
        return output
    }
}