package dataComponents

class Dish(val name: String,
           var price: Int,
           private var cookingTime: Int,
           var rating: DishRating) {

    fun getPrice(): Int {
       return price
    }


    fun getCookingTime(): Int {
        return cookingTime
    }

    fun changePrice(newPrice: Int) {
        price = newPrice
    }

    fun changeCookingTime(newCookingTime: Int) {
        cookingTime = newCookingTime
    }

    override fun toString(): String {
        return "$name \$$price | ~$cookingTime min."
    }

}