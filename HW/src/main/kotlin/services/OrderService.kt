package services

import dataComponents.Dish
import dataComponents.Order
import repositories.Menu
import repositories.OrderRepository
import repositories.StatisticsRepository

class OrderService(
    private val orderRepository: OrderRepository,
    private val statsRepository: StatisticsRepository,
    private val menu: Menu,
) {
    fun tryMakeOrder(username: String, listOfNames: List<String>): String {
        var output = "Checking order...\n"
        var newDish: Dish
        val listOfDishes: MutableList<Dish> = mutableListOf()
        for (dishName in listOfNames) {
            try {
                newDish = menu.getDishByName(dishName)
                menu.orderDish(newDish)
            } catch (e: NoSuchElementException) {
                output += "WARNING: ${e.message}\n"
                continue
            }
            listOfDishes.add(newDish)
            output += "Dish $dishName added to the order\n"
        }
        if (listOfDishes.isEmpty()) {
            return "ERROR: No correct dishes! The order has been canceled."
        }

        val orderId = orderRepository.acceptOrder(username, listOfDishes)
        statsRepository.addOrder()
        output += "THE ORDER №$orderId IS ACCEPTED! DON'T FORGET THE NUMBER OF YOUR ORDER!"
        return output
    }

    fun tryCancelOrder(username: String, orderId: Int): String {
        val dishes: List<Dish>
        val output = "Canceling order... "
        try {
            val order = orderRepository.checkUserAccessToOrder(username, orderId)
            dishes = orderRepository.cancelOrder(order)
        } catch (e: NoSuchElementException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        } catch (e: AccessDeniedException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        }
        for (dish in dishes) {
            menu.returnDish(dish)
        }
        statsRepository.subtractOrder()
        return output + "SUCCESS"
    }

    fun tryAddDishToOrder(username: String, orderId: Int, dishName: String): String {
        val output = "Trying to add a new dish... "
        try {
            val order = orderRepository.checkUserAccessToOrder(username, orderId)
            orderRepository.addDishToOrder(order, menu.getDishByName(dishName))
        } catch (e: NoSuchElementException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        } catch (e: AccessDeniedException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"

        } catch (e: Exception) {
            return "Trying to add a new dish... FAIL\n" +
                    "ERROR: ${e.message}"

        }
        return output + "SUCCESS"
    }

    fun tryToStartProcessingOrder(orderId: Int): String {
        try {
            orderRepository.startPreparingOrder(orderId)
        } catch (e: NoSuchElementException) {
            return "ERROR: ${e.message}"
        }
        return "Order $orderId in process..."
    }

    fun tryToFinishOrder(orderId: Int): String {
        try {
            orderRepository.finishOrder(orderId)
        } catch (e: NoSuchElementException) {
            return "ERROR: ${e.message}"
        }
        return "Order $orderId is finished"
    }

    fun tryToPayForOrder(username: String, orderId: Int): String {
        val output = "Trying to pay the order... "
        try {
            val order = orderRepository.checkUserAccessToOrder(username, orderId)
            val cost = orderRepository.payForOrder(order)
            statsRepository.increaseRevenue(cost)
        } catch (e: NoSuchElementException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        } catch (e: AccessDeniedException) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"

        }
        statsRepository.addPaidOrder()
        return output + "SUCCESS"
    }

}