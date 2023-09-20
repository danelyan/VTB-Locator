package ru.vdnh.backend.template.tests.core.persistance.sql

import ru.vdnh.backend.template.tests.core.model.Event
import ru.vdnh.backend.template.tests.core.persistance.EventRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

internal class EventsRepositoryImpl : EventRepository {

    override fun list(): List<Event> {
        return EventMappingsTable.selectAll().map { toModel(it) }
    }

    override fun delete(eventNo: Long) {
        EventMappingsTable.deleteWhere { EventMappingsTable.eventNo eq eventNo }
    }

    override fun count(): Int {
        return EventMappingsTable.selectAll().count().toInt()
    }

    override fun getPartsForCar(placeId: Long): List<Event> {
        return EventMappingsTable.select { EventMappingsTable.placeId eq placeId }.map { toModel(it) }
    }

    override fun addPartToCar(placeId: Long, event: Event): Event {
        EventMappingsTable.insert {
            it[EventMappingsTable.placeId] = placeId
            it[eventNo] = event.eventNo
            it[description] = event.description
            it[manufacturer] = event.description
        }
        return event
    }

    private fun toModel(row: ResultRow): Event {
        return Event(
            eventNo = row[EventMappingsTable.eventNo],
            manufacturer = row[EventMappingsTable.manufacturer],
            description = row[EventMappingsTable.description]
        )
    }
}
