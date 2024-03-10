package cs.hse.hw.repositories

import cs.hse.hw.dataComponents.Dish
import org.springframework.stereotype.Repository
import cs.hse.hw.serializationModule.Serializer
import kotlin.Exception

@Repository
class Menu(private var listOfDishes: MutableMap<Dish, Int> = mutableMapOf(),
           private val pathToDishesFile: String = "menu_dishes.json",
           private val pathToCountsOfDishesFile: String = "menu_counts.json") {
    private val serializer: Serializer = Serializer(pathToDishesFile)

    init {
        deserializeMenu()
    }

    fun serialiseMenu() {
        val dishes = listOfDishes.keys
        val countsOfDishes = listOfDishes.values

        serializer.serializeListToFile(dishes.toList())
        serializer.serializeListToFile(countsOfDishes.toList(), pathToCountsOfDishesFile)
    }

    private fun deserializeMenu() {
        val dishes = serializer.deserializeListFromFile<Dish>()
        val countOfDishes = serializer.deserializeListFromFile<Int>(pathToCountsOfDishesFile)

        listOfDishes = dishes.zip(countOfDishes).toMap().toMutableMap()
    }

    fun addDish(dish: Dish, countOfDishes: Int) {
        checkDishCount(dish, countOfDishes)
        checkDishPrice(dish, dish.price)
        checkDishCookingTime(dish, dish.cookingTime)
        if (listOfDishes.any { it.key.name == dish.name }) {
            throw Exception("The dish with this name already exists!")
        }
        listOfDishes[dish] = countOfDishes
    }

    fun deleteDish(dish: Dish) {
        listOfDishes.remove(dish)
    }

    fun orderDish(dish: Dish): Dish {
        val countOfDishes = listOfDishes[dish]!!
        if (countOfDishes < 1) {
            throw NoSuchElementException("No dishes \"${dish.name}\" left!")
        }
        listOfDishes[dish] = countOfDishes - 1
        return dish
    }

    fun returnDish(dish: Dish) {
        val countOfDishes = listOfDishes[dish]
        if (countOfDishes == null) {
            listOfDishes[dish] = 1
        } else {
            listOfDishes[dish] = countOfDishes + 1
        }
    }

    fun changeDishCount(dish: Dish, countOfDishes: Int) {
        checkDishCount(dish, countOfDishes)
        listOfDishes[dish] = countOfDishes
    }

    private fun checkDishCount(dish: Dish, countOfDishes: Int) {
        if (countOfDishes < 0) {
            throw IllegalArgumentException("Count of dishes can't be negative!")
        }
    }

    fun changeDishPrice(dish: Dish, newPrice: Int) {
        checkDishPrice(dish, newPrice)
        dish.price = newPrice
    }

    private fun checkDishPrice(dish: Dish, price: Int) {
        if (price <= 0) {
            throw IllegalArgumentException("Price should be grater then 0!")
        }
    }

    fun changeDishCookingTime(dish: Dish, newTime: Int) {
        checkDishCookingTime(dish, newTime)
        dish.cookingTime = newTime
    }

    private fun checkDishCookingTime(dish: Dish, time: Int) {
        if (time < 5) {
            throw IllegalArgumentException("Cooking time should be grater then 4!")
        }
    }

    fun getDishByName(dishName: String): Dish {
        return listOfDishes.keys.find { it.name == dishName }
            ?: throw NoSuchElementException("No dish with name \"$dishName\" in menu!")
    }

    fun getTopDishes(): List<Dish> {
        val sortedDishes = listOfDishes.keys.sortedBy { it.rating.rating }
        if (sortedDishes.size < 4) {
            throw Exception("Not enough dishes in menu")
        }
        return sortedDishes.subList(0, 4)
    }

    fun getAverageRating() : Double {
        val countOfDishes = listOfDishes.keys.size
        if(countOfDishes == 0) {
            return 0.0
        }
        val sum = listOfDishes.keys.sumOf { it.rating.rating }
        return sum / countOfDishes
    }

    fun getAllReviews(dish: Dish) : List<String> {
        return dish.rating.getReviews()
    }

    override fun toString(): String {
        var output = "--------------------- Menu ---------------------\n"
        for (line in listOfDishes) {
            output += "${line.key} -- ${line.value} pc. left\n"
        }
        output += "------------------------------------------------\n"
        return output
    }
}