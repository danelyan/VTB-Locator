package ru.vtb24.mobilebanking.android.locator.data

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
) {

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    fun updateCurrentLocation(currentLocation: Location) {
        _currentLocation.value = currentLocation
    }

}
