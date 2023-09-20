package ru.vdnh.backend.template.tests.core.persistance.sql

import org.jetbrains.exposed.dao.id.LongIdTable

internal object PlaceMappingsTable : LongIdTable("places") {
    val name = varchar("brand", length = 255)
    val type = varchar("model", length = 255)
}
