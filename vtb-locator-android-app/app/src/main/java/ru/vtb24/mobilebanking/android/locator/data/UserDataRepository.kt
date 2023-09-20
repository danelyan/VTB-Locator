package ru.vtb24.mobilebanking.android.locator.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.vtb24.mobilebanking.android.locator.domain.model.Restaurant

class UserDataRepository() {
    suspend fun getLikedRestaurants(): Flow<List<Restaurant>> {
        return flow {
            restaurantList
        }
    }

}