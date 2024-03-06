package services

import dataComponents.Dish
import dataComponents.DishRating
import repositories.Menu

class MenuService(private val menu: Menu) {
    fun tryToAddDishToMenu(
        dishName: String,
        dishPrice: Int,
        dishCookingTime: Int,
        countOfDishes: Int,
    ): String {
        val output = "Trying to add dish to menu... "
        val dish = Dish(dishName, dishPrice, dishCookingTime, DishRating())
        try {
            menu.addDish(dish, countOfDishes)
        } catch (e: IllegalArgumentException) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
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
        } catch (e: NoSuchElementException) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
        } catch (e: IllegalArgumentException) {
            return output + "FAIL\n" +
                    "ERROR: " + e.message
        }
        return output + "SUCCESS"
    }
}