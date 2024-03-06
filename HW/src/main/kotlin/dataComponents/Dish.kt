package dataComponents

import kotlinx.serialization.Serializable

@Serializable
class Dish(val name: String,
           var price: Int,
           private var cookingTime: Int,
           var rating: DishRating) {

    fun getPriceT(): Int {
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