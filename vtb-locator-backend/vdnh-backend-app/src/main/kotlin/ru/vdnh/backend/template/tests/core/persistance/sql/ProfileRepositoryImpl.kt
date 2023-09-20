package ru.vdnh.backend.template.tests.core.persistance.sql

import ru.vdnh.backend.template.exception.AppException
import ru.vdnh.backend.template.exception.ErrorCodes
import ru.vdnh.backend.template.pagination.PageRequest
import ru.vdnh.backend.template.pagination.SortField
import ru.vdnh.backend.template.tests.core.model.Profile
import ru.vdnh.backend.template.tests.core.model.Persons
import ru.vdnh.backend.template.tests.core.persistance.ProfileRepository
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder

class ProfileRepositoryImpl : ProfileRepository {

    override fun save(profile: Profile): Profile {
        return Profile.new { profile }
    }

    override fun update(profile: Profile): Profile {
        val dbPerson = getById(profile.id.value)!!
        dbPerson.name = profile.name
        dbPerson.birthday = profile.birthday
        return dbPerson
    }

    override fun getById(id: Int): Profile? {
        return Profile.findById(id)
    }

    override fun delete(id: Int) {
        getById(id)!!.delete()
    }

    override fun count(pageRequest: PageRequest): Int {
        return Profile
            .all()
            .limit(pageRequest.limit, pageRequest.offset.toLong())
            .count()
            .toInt()
    }

    override fun list(pageRequest: PageRequest): List<Profile> {
        val sorts = pageRequest.sort
            .map { sortToColumn(it) }
            .toTypedArray()

        return Profile
            .all()
            .limit(pageRequest.limit, pageRequest.offset.toLong())
            .orderBy(*sorts)
            .toList()
    }

    private fun sortToColumn(sort: SortField): Pair<Column<*>, SortOrder> {
        // TODO we should use reflection here
        val field = when (sort.field) {
            "name" -> Persons.name
            "birthday" -> Persons.birthday
            else -> throw AppException(
                ErrorCodes.NotImplemented,
                "Sort by column '${sort.field}' in Person table not " +
                    "implemented."
            )
        }
        return field to SortOrder.valueOf(sort.order.name.uppercase())
    }
}
