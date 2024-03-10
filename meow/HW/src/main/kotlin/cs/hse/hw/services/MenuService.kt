package cs.hse.hw.services

import cs.hse.hw.dataComponents.Dish
import org.springframework.stereotype.Service
import cs.hse.hw.repositories.Menu
import kotlinx.serialization.SerializationException

@Service
class MenuService(private val menu: Menu) {
    fun tryToAddDishToMenu(dish: Dish, countOfDishes: Int): String {
        val output = "Trying to add dish to menu... "
        try {
            menu.addDish(dish, countOfDishes)
        } catch (e: Exception) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
        }
        return output + "SUCCESS"
    }

    fun tryToDeleteDishFromMenu(dishName: String): String {
        val output = "Trying to delete dish from menu... "
        try {
            val dish = menu.getDishByName(dishName)
            menu.deleteDish(dish)
        } catch (e: NoSuchElementException) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
        }
        return output + "SUCCESS"
    }

    fun tryToChangeDishCount(dishName: String, countOfDishes: Int): String {
        val output = "Trying to change dish count... "
        try {
            val dish = menu.getDishByName(dishName)
            menu.changeDishCount(dish, countOfDishes)
        } catch (e: Exception) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
        }
        return output + "SUCCESS"
    }

    fun tryToChangeDishPrice(dishName: String, newPrice: Int): String {
        val output = "Trying to change dish price... "
        try {
            val dish = menu.getDishByName(dishName)
            menu.changeDishPrice(dish, newPrice)
        } catch (e: Exception) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
        }
        return output + "SUCCESS"
    }

    fun tryToChangeDishCookingTime(dishName: String, newCookingTime: Int): String {
        val output = "Trying to change dish cooking time... "
        try {
            val dish = menu.getDishByName(dishName)
            menu.changeDishCookingTime(dish, newCookingTime)
        } catch (e: Exception) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
        }
        return output + "SUCCESS"
    }

    fun getMenu(): String {
        return menu.toString()
    }

    fun serialiseMenu(): String {
        try {
            menu.serialiseMenu()
        } catch (e: SerializationException) {
            return e.message!!
        }
        return "Serialization... SUCCESS"
    }
}