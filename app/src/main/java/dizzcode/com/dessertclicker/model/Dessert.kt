package dizzcode.com.dessertclicker.model

import androidx.annotation.DrawableRes

/**
 * [Dessert] is the data class to represent the Dessert imageId, price, and startProductionAmount
 */
data class Dessert(
    @DrawableRes val imageId: Int,
    val price: Int,
    val startProductionAmount: Int
)
