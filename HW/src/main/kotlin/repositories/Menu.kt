package repositories

import dataComponents.Dish
import dataComponents.FuckingSerializer
import java.lang.Exception

class Menu(private var listOfDishes: MutableMap<Dish, Int> = mutableMapOf(), private val serializer: FuckingSerializer) {
    fun addDish(dish: Dish, countOfDishes: Int) {
        if (countOfDishes < 0) {
            throw IllegalArgumentException("Count of dishes can't be negative!")
        }
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
        if (countOfDishes < 0) {
            throw IllegalArgumentException("Count of dishes can't be negative!")
        }
        listOfDishes[dish] = countOfDishes
    }

    fun getDishByName(dishName: String): Dish {
        return listOfDishes.keys.find { it.name == dishName }
            ?: throw NoSuchElementException("No dish with name \"$dishName\" in menu!")
    }

    fun getTopDishes(): List<Dish> {
        val sortedDishes = listOfDishes.keys.sortedBy { it.rating.rating }
        return sortedDishes.subList(0, 4)
    }

    fun getAverageRating() : Double {
        val sum = listOfDishes.keys.sumOf { it.rating.rating }
        return sum / listOfDishes.keys.size
    }

    fun getAllReviews(dish: Dish) : List<String> {
        return dish.rating.getReviews()
    }

    fun serialiseMenu() {
        val dishes = listOfDishes.keys
        val countsOfDishes = listOfDishes.values


        serializer.serializeListToFile(dishes.toList(), "notFuckingDishes.json")
        serializer.serializeListToFile(countsOfDishes.toList(), "countsOfDishes.json")
    }

    fun deserializeMenu() {
        val dishes = serializer.deserializeListFromFile<Dish>("notFuckingDishes.json")
        val countOfDishes = serializer.deserializeListFromFile<Int>("countsOfDishes.json")

        listOfDishes = dishes.zip(countOfDishes).toMap().toMutableMap()
    }

    override fun toString(): String {
        var output = "-------------- Menu --------------\n"
        for (line in listOfDishes) {
            output += "${line.key}\t--\t${line.value} pc. left\n"
        }
        output += "----------------------------------\n"
        return output
    }
}