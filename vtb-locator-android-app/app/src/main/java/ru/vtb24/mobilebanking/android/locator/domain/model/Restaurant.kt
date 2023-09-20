package ru.vtb24.mobilebanking.android.locator.domain.model

import ru.vtb24.mobilebanking.android.optimum.domain.model.MenuItem

data class Restaurant(
    val name: String,
    val rating: Double,
    val noOfRatings: Int,
    val timeInMillis: Long,
    val variety: String,
    val place: String,
    val averagePrice: Double,
    val image: Int,
    val menu: List<MenuItem>
)
