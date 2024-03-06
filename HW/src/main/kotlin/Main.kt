import dataComponents.Dish
import dataComponents.DishRating
import dataComponents.FuckingSerializer
import dataComponents.User
import kotlinx.serialization.*
import kotlinx.coroutines.job
import repositories.Menu
import repositories.StatisticsRepository
import repositories.UserRepository

fun main(args: Array<String>) {
    var s = StatisticsRepository()
    s.deserializeList()

    s.addUser()
    s.addPaidOrder()
    s.increaseRevenue(100)


    s.serialiseList()

}