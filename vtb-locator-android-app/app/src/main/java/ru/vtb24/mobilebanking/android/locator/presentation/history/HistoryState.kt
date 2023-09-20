package ru.vtb24.mobilebanking.android.locator.presentation.history

import ru.vtb24.mobilebanking.android.locator.domain.model.Restaurant


data class HistoryState(
    val likedRestaurantList: List<Restaurant> = emptyList(),
)
