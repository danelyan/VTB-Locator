package ru.vtb24.mobilebanking.android.locator.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.vtb24.mobilebanking.android.locator.data.HomeRepository
import ru.vtb24.mobilebanking.android.locator.data.LocationRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    locationRepository: LocationRepository,
//    private val userDataRepository: UserDataRepository,
//    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _currentPosition = locationRepository.currentLocation
    val currentPosition = _currentPosition.map {
        Point(it?.latitude ?: 55.751574, it?.longitude ?: 37.573856)
    }
    private val _homeScreenState = mutableStateOf(
        HomeScreenState()
    )
    val homeScreenState: State<HomeScreenState> = _homeScreenState

    init {
//        viewModelScope.launch {
//            when (val result = repository.getAds()) {
//                is Results.Success -> _homeScreenState.value = homeScreenState.value.copy(
//                    adsList = result.data
//                )
//
//                is Results.Error -> {
//                }
//            }
//
//            when (val result = repository.getFoodItems()) {
//                is Results.Success -> _homeScreenState.value = homeScreenState.value.copy(
//                    foodList = result.data
//                )
//
//                is Results.Error -> {
//
//                }
//            }
//
//            when (val result = repository.getRestaurants()) {
//                is Results.Success -> _homeScreenState.value = homeScreenState.value.copy(
//                    restaurantList = result.data,
//                )
//
//                is Results.Error -> {
//
//                }
//            }
//
//            userDataRepository.getLikedRestaurants().collect {
//                _homeScreenState.value = homeScreenState.value.copy(
//                    likedRestaurantList = it
//                )
//            }
//        }
    }

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.SelectRestaurant -> {
                viewModelScope.launch {
//                    cartRepository.setRestaurant(event.restaurant)
                    event.onClick()
                }
            }
        }
    }


}