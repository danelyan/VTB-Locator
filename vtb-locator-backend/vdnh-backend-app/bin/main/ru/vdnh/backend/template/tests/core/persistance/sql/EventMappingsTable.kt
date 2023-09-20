package ru.vdnh.backend.template.tests.core.persistance.sql

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

internal object EventMappingsTable : Table("events") {
    val eventNo = long("event_no")
    val placeId = reference("place_id", PlaceMappingsTable.id, ReferenceOption.CASCADE)
    val manufacturer = varchar("manufacturer", length = 255)
    val description = text("description")

    override val primaryKey = PrimaryKey(eventNo)

    init {
        uniqueIndex(eventNo, placeId)
    }
}
