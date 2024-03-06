package dataComponents

import kotlinx.serialization.Serializable

@Serializable
class DishRating {
    private val listOfReviews : MutableList<String> = mutableListOf()
    var rating: Double = 0.0

    fun addReview(score: Int, text: String) {
        if (score !in 1..5) {
            throw IllegalArgumentException("Rating should be from 1 to 5!")
        }
        rating *= listOfReviews.size
        listOfReviews.add(text)
        rating = (rating + score) / listOfReviews.size
    }

    fun getReviews() : List<String> {
        return listOfReviews
    }
}