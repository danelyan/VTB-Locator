package ru.vdnh.backend.template.tests.core.persistance.sql

import ru.vdnh.backend.template.database.createSorts
import ru.vdnh.backend.template.database.fromFilters
import ru.vdnh.backend.template.pagination.PageRequest
import ru.vdnh.backend.template.tests.core.model.Place
import ru.vdnh.backend.template.tests.core.model.PlaceSaveCommand
import ru.vdnh.backend.template.tests.core.persistance.PlaceRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class PlaceRepositoryImpl : PlaceRepository {

    override fun exists(id: Long): Boolean {
        return PlaceMappingsTable.select { PlaceMappingsTable.id eq id }.count() == 1L
    }

    override fun getById(id: Long): Place? {
        val rst = PlaceMappingsTable.select { PlaceMappingsTable.id eq id }.singleOrNull()
        return if (rst != null) {
            resultToModel(rst)
        } else {
            null
        }
    }

    override fun save(place: PlaceSaveCommand): Place {
        val newCarId = PlaceMappingsTable.insert {
            it[name] = place.name
            it[type] = place.type
        } get PlaceMappingsTable.id

        return Place(newCarId.value, place.name, place.type)
    }

    override fun update(place: Place): Place {
        PlaceMappingsTable.update({ PlaceMappingsTable.id eq place.id }) {
            it[name] = place.name
            it[type] = place.type
        }
        return getById(place.id)!!
    }

    override fun count(pageRequest: PageRequest): Int {
        return PlaceMappingsTable.fromFilters(pageRequest.filter).count().toInt()
    }

    override fun delete(id: Long) {
        PlaceMappingsTable.deleteWhere { PlaceMappingsTable.id eq id }
    }

    override fun list(pageRequest: PageRequest): List<Place> {
        return PlaceMappingsTable
            .fromFilters(pageRequest.filter)
            .limit(pageRequest.limit, pageRequest.offset.toLong())
            .orderBy(*PlaceMappingsTable.createSorts(pageRequest.sort).toTypedArray())
            .map { resultToModel(it) }
    }

    private fun resultToModel(rstRow: ResultRow): Place {
        return Place(
            rstRow[PlaceMappingsTable.id].value,
            rstRow[PlaceMappingsTable.name],
            rstRow[PlaceMappingsTable.type]
        )
    }
}
