package repositories

import dataComponents.Dish
import dataComponents.Order
import dataComponents.OrderStatus
import java.nio.file.AccessDeniedException

class OrderRepository {
    private var orders: MutableList<Order> = mutableListOf()

    fun acceptOrder(username: String, listOfDishes: MutableList<Dish>) : Int {
        val id = getNewId()
        orders.add(Order(id, username, listOfDishes, OrderStatus.ACCEPTED))
        return id
    }

    fun startPreparingOrder(orderId: Int) {
        val order = getOrderIfExists(orderId)
        order.status = OrderStatus.PREPARING
    }

    fun finishOrder(orderId: Int) {
        val order = getOrderIfExists(orderId)
        order.status = OrderStatus.READY
    }

    fun cancelOrder(order: Order) : List<Dish> {
        orders.remove(order)
        return order.listOfDishes
    }

    fun addDishToOrder(order: Order, dish: Dish) {
        order.addDish(dish)
    }

    fun payForOrder(order: Order) : Int {
        order.status = OrderStatus.PAID
        return order.getTotalCost()
    }

    private fun getOrderIfExists(orderId: Int) : Order {
        return orders.find { it.id == orderId }
            ?: throw NoSuchElementException("No order with id $orderId")
    }

    private fun getNewId(): Int {
        if(orders.isEmpty()) {
            return 0
        }
        return (orders.maxOf { it.id } + 1)
    }

    fun checkUserAccessToOrder(username: String, orderId: Int): Order {
        val order = getOrderIfExists(orderId)
        if (username != order.ownerUsername) {
            throw AccessDeniedException("You have no access to someone else's order!")
        }
        return order
    }

    override fun toString(): String {
        var output = "------------- Orders -------------\n"
        for (order in orders) {
            output += "№${order.id} | \$${order.getTotalCost()} | is ${order.status} \n"
        }
        output += "----------------------------------\n"
        return output
    }
}