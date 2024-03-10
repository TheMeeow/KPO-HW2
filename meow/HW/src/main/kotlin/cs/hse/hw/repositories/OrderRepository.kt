package cs.hse.hw.repositories

import cs.hse.hw.dataComponents.Dish
import com.example.hw.dataComponents.Order
import com.example.hw.dataComponents.OrderStatus
import org.springframework.stereotype.Repository
import java.nio.file.AccessDeniedException

@Repository
class OrderRepository {
    private var orders: MutableList<Order> = mutableListOf()

    fun acceptOrder(username: String, listOfDishes: MutableList<Dish>) : Order {
        val id = getNewId()
        val order = Order(id, username, listOfDishes, OrderStatus.ACCEPTED)
        orders.add(order)
        return order
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

    fun removeDishFromOrder(order: Order, dish: Dish) {
        order.listOfDishes.remove(dish)
    }

    fun payForOrder(order: Order) : Int {
        if(order.status == OrderStatus.PAID) {
            throw Exception("The order is already paid")
        }
        if(order.status != OrderStatus.READY) {
            throw Exception("You can't pay for a not ready order")
        }
        order.status = OrderStatus.PAID
        return order.getTotalCost()
    }

    fun getOrderIfExists(orderId: Int) : Order {
        return orders.find { it.id == orderId }
            ?: throw NoSuchElementException("No order with id $orderId")
    }

    fun getNextAcceptedOrder(): Order? {
        return orders.find { it.status == OrderStatus.ACCEPTED }
    }

    private fun getNewId(): Int {
        if(orders.isEmpty()) {
            return 1
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