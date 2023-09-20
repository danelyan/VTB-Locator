package ru.vtb24.mobilebanking.android.locator.presentation.home

import ru.vtb24.mobilebanking.android.locator.domain.model.Advertisement
import ru.vtb24.mobilebanking.android.locator.domain.model.FoodItem
import ru.vtb24.mobilebanking.android.locator.domain.model.Restaurant

data class HomeScreenState(
    val adsList: List<Advertisement> = emptyList(),
    val foodList: List<FoodItem> = emptyList(),
    val likedRestaurantList: List<Restaurant> = emptyList(),
    val restaurantList: List<Restaurant> = emptyList(),
)
