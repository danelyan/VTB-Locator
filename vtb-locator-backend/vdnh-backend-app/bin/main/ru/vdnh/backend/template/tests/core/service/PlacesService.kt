package ru.vdnh.backend.template.tests.core.service

import ru.vdnh.backend.template.pagination.PageRequest
import ru.vdnh.backend.template.tests.core.model.Place
import ru.vdnh.backend.template.tests.core.model.PlaceSaveCommand

interface PlacesService {
    suspend fun count(pageRequest: PageRequest): Int
    suspend fun getPlaceById(placeId: Long): Place?
    suspend fun insertNewPlace(newCar: PlaceSaveCommand): Place
    suspend fun updatePlace(place: Place): Place
    suspend fun list(pageRequest: PageRequest): List<Place>
}
