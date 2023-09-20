package ru.vdnh.backend.template.tests.core.persistance

import ru.vdnh.backend.template.tests.core.model.Event

interface EventRepository {
    fun list(): List<Event>
    fun delete(eventNo: Long)
    fun count(): Int
    fun getPartsForCar(placeId: Long): List<Event>
    fun addPartToCar(placeId: Long, event: Event): Event
}
