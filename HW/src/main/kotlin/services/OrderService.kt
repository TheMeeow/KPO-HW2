package services

import dataComponents.Dish
import repositories.Menu
import repositories.OrderRepository
import repositories.StatisticsRepository

class OrderService(private val orderRepository: OrderRepository,
                   private val statsRepository : StatisticsRepository,
                   private val menu: Menu) {
    fun tryMakeOrder(username: String, listOfNames: List<String>): String {
        var output = "Checking order...\n"
        var newDish: Dish
        val listOfDishes: MutableList<Dish> = mutableListOf()
        for (dishName in listOfNames) {
            try {
                newDish = menu.orderDish(dishName)
            } catch (e: NoSuchElementException) {
                output += "WARNING: ${e.message}\n"
                continue
            }
            listOfDishes.add(newDish)
            output += "Dish $dishName added to the order\n"
        }
        if (listOfDishes.isEmpty()) {
            throw Exception("ERROR: No correct dishes! The order has been canceled.")
        }

        val orderId = orderRepository.acceptOrder(username, listOfDishes)
        statsRepository.addOrder()
        output += "THE ORDER №$orderId IS ACCEPTED! DON'T FORGET THE NUMBER OF YOUR ORDER!"
        return output
    }

    fun tryCancelOrder(username: String, orderId: Int): String {
        val dishes: List<Dish>
        try {
            dishes = orderRepository.cancelOrder(username, orderId)
        } catch (e: NoSuchElementException) {
            return "Canceling order... FAIL\n" +
                    "ERROR: ${e.message}"
        } catch (e: AccessDeniedException) {
            return "Canceling order... FAIL\n" +
                    "ERROR: ${e.message}"
        }
        for (dish in dishes) {
            menu.returnDish(dish)
        }
        statsRepository.subtractOrder()
        return "Canceling order... SUCCESS"
    }

    fun tryAddDishToOrder(username: String, orderId: Int, dishName: String): String {
        try {
            orderRepository.addDishToOrder(username, orderId, menu.getDishByName(dishName))
        } catch (e: NoSuchElementException) {
            return "Trying to add a new dish... FAIL\n" +
                    "ERROR: ${e.message}"
        } catch (e: AccessDeniedException) {
            return "Trying to add a new dish... FAIL\n" +
                    "ERROR: ${e.message}"

        } catch (e: Exception) {
            return "Trying to add a new dish... FAIL\n" +
                    "ERROR: ${e.message}"

        }
        return "Trying to add a new dish... SUCCESS"

    }

    fun tryToStartProcessingOrder(orderId: Int) : String {
        try {
            orderRepository.startPreparingOrder(orderId)
        } catch (e: NoSuchElementException) {
            return "ERROR: ${e.message}"
        }
        return "Order $orderId in process..."
    }

    fun tryToPayForOrder(username: String, orderId: Int) {
        val cost = orderRepository.payForOrder(username, orderId)
        statsRepository.increaseRevenue(cost)
        statsRepository.addPaidOrder()
    }


}