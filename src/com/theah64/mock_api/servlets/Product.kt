package com.theah64.mock_api.servlets

/**
 * Created by theapache64 on 15/11/17.
 */
class Product(
        val title: String,
        val brand: String,
        val thumbUrl: String,
        val imageUrl: String,
        val quantities: List<Quantity>,
        val description: String,
        val categoryName: String,
        val quantityTitle: String,
        val similarProducts: List<SimilarProduct>
) {

    class Quantity(
            val id: String,
            val povId: String,
            val quantity: String,
            val cartCount: String,
            val realPrice: String,
            val anonaPrice: String,
            val availability: String,
            val rewardsNeeded: String,
            val favoritesCount: String,
            val purchasedCount: String
    )

    class SimilarProduct(
            val id: String,
            val title: String,
            val thumbUrl: String
    )


}


