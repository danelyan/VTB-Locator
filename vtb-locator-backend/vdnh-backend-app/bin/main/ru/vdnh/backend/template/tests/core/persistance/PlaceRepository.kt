package ru.vdnh.backend.template.tests.core.persistance

import ru.vdnh.backend.template.pagination.PageRequest
import ru.vdnh.backend.template.tests.core.model.Place
import ru.vdnh.backend.template.tests.core.model.PlaceSaveCommand

interface PlaceRepository {
    fun exists(id: Long): Boolean
    fun save(place: PlaceSaveCommand): Place
    fun update(place: Place): Place
    fun getById(id: Long): Place?
    fun count(pageRequest: PageRequest = PageRequest()): Int
    fun delete(id: Long)
    fun list(pageRequest: PageRequest = PageRequest()): List<Place>
}
