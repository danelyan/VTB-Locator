package ru.vtb24.mobilebanking.android.locator.presentation.history

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vtb24.mobilebanking.android.locator.data.UserDataRepository
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _likedRestaurants = mutableStateOf(HistoryState())
    val likedRestaurants: State<HistoryState> = _likedRestaurants

    init {
        viewModelScope.launch {
            userDataRepository.getLikedRestaurants().collect {
                _likedRestaurants.value = likedRestaurants.value.copy(
                    likedRestaurantList = it
                )

            }

        }
    }

}