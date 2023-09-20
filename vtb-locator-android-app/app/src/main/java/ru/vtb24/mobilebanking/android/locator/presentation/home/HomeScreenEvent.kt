package ru.vtb24.mobilebanking.android.locator.presentation.home

import ru.vtb24.mobilebanking.android.locator.domain.model.Restaurant

sealed class HomeScreenEvent {
    data class SelectRestaurant(val restaurant: Restaurant, val onClick: () -> Unit) :
        HomeScreenEvent()
}