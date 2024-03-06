package dataComponents

class Order(val id: Int, val ownerUsername: String, val listOfDishes: MutableList<Dish>, var status: OrderStatus) {
    fun getTotalCost(): Int {
        var sum = 0
        for (dish in listOfDishes) {
            sum += dish.getPriceT()
        }
        return sum
    }

    fun addDish(dish: Dish) {
        if (status == OrderStatus.PAID || status == OrderStatus.READY) {
            throw Exception("Can't add a dish to the finished order!")
        }
        listOfDishes.add(dish)
    }
}