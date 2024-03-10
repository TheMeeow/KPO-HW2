package cs.hse.hw.dataComponents

import kotlinx.serialization.Serializable

@Serializable
class Dish(val name: String,
           var price: Int,
           var cookingTime: Int,
           var rating: DishRating = DishRating()) {

    override fun toString(): String {
        return "$name \$$price | ~$cookingTime min."
    }

}