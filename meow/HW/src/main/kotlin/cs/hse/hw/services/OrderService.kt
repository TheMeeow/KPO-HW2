package cs.hse.hw.services

import com.example.hw.dataComponents.Order
import com.example.hw.dataComponents.OrderStatus
import cs.hse.hw.dataComponents.Dish
import cs.hse.hw.dataComponents.User
import org.springframework.stereotype.Service
import cs.hse.hw.repositories.Menu
import cs.hse.hw.repositories.OrderRepository
import cs.hse.hw.repositories.StatisticsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Service
class OrderService(
        private val orderRepository: OrderRepository,
        private val statsRepository: StatisticsRepository,
        private val menu: Menu,
) {
    private val maxCountOfProcessingOrders: Int = 2
    private var ordersInProcess: Int = 0
    private val kitchenMutex = Mutex()
    private val ordersList = mutableMapOf<Int, Job>()

    @OptIn(DelicateCoroutinesApi::class)
    fun tryToStartProcess(order: Order) {
        if (ordersInProcess == maxCountOfProcessingOrders) {
            return
        }
        val job = GlobalScope.launch { startPreparingOrder(order) }
        ordersList[order.id] = job
        ordersInProcess++
    }

    suspend fun startPreparingOrder(order: Order) {
        try {
            kitchenMutex.withLock {
                order.status = OrderStatus.PREPARING
            }
            var countOfPreparedDishes = 0
            var countOfDishesInOrder: Int
            var currentDish: Dish
            do {
                kitchenMutex.withLock {
                    countOfDishesInOrder = order.listOfDishes.size
                    currentDish = order.listOfDishes[countOfPreparedDishes]
                }
                delay(currentDish.cookingTime * 1000L)
                countOfPreparedDishes++
            } while (countOfPreparedDishes < countOfDishesInOrder)
            kitchenMutex.withLock {
                order.status = OrderStatus.READY
            }
            checkNextOrder()
        } catch (e: CancellationException) {
            println("Order ${order.id} canceled!")
        }
    }

    fun checkNextOrder() {
        ordersInProcess--
        val nextOrder = orderRepository.getNextAcceptedOrder()
        if (nextOrder != null) {
            tryToStartProcess(nextOrder)
        }
    }

    fun tryMakeOrder(username: String, listOfDishesNames: List<String>): String {
        var output = "Checking order...\n"
        var newDish: Dish
        val listOfDishes: MutableList<Dish> = mutableListOf()
        for (dishName in listOfDishesNames) {
            try {
                newDish = menu.getDishByName(dishName)
                menu.orderDish(newDish)
            } catch (e: NoSuchElementException) {
                output += "WARNING: ${e.message}\n"
                println(output)
                continue
            }
            listOfDishes.add(newDish)
            output += "Dish $dishName added to the order\n"
        }
        if (listOfDishes.isEmpty()) {
            return output + "ERROR: No correct dishes! The order has been canceled."
        }

        val order = orderRepository.acceptOrder(username, listOfDishes)
        statsRepository.addOrder()
        output += "THE ORDER №${order.id} IS ACCEPTED! DON'T FORGET THE NUMBER OF YOUR ORDER!"
        tryToStartProcess(order)

        return output
    }

    fun tryCancelOrder(username: String, orderId: Int): String {
        val dishes: List<Dish>
        val output = "Canceling order... "
        try {
            val order = orderRepository.checkUserAccessToOrder(username, orderId)
            ordersList[orderId]!!.cancel()
            ordersList.remove(orderId)
            dishes = orderRepository.cancelOrder(order)
        } catch (e: Exception) {
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
        val order: Order
        val dish: Dish

        try {
            order = orderRepository.checkUserAccessToOrder(username, orderId)
            dish = menu.getDishByName(dishName)
            orderRepository.addDishToOrder(order, dish)
        } catch (e: Exception) {
            return "Trying to add a new dish... FAIL\n" +
                    "ERROR: ${e.message}"

        }

        try {
            menu.orderDish(dish)
        } catch (e: NoSuchElementException) {
            orderRepository.removeDishFromOrder(order, dish)
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        }
        return output + "SUCCESS"
    }


    fun getOrderStatus(user: User, orderId: Int): String {
        val output = "Trying to show order status... "
        val order: Order
        try {
            order = if(user.isAdmin) {
                orderRepository.getOrderIfExists(orderId)
            } else {
                orderRepository.checkUserAccessToOrder(user.username, orderId)
            }
        } catch (e: Exception) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        }
        return output + "SUCCESS" +
                "\nOrder is ${order.status}"
    }


    fun tryToPayForOrder(username: String, orderId: Int): String {
        val output = "Trying to pay the order... "
        try {
            val order = orderRepository.checkUserAccessToOrder(username, orderId)
            val cost = orderRepository.payForOrder(order)
            statsRepository.increaseRevenue(cost)
        } catch (e: Exception) {
            return output + "FAIL\n" +
                    "ERROR: ${e.message}"
        }
        statsRepository.addPaidOrder()
        return output + "SUCCESS"
    }

}