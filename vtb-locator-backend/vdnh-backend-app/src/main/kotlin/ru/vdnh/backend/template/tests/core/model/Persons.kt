package ru.vdnh.backend.template.tests.core.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Persons : IntIdTable() {
    val name = varchar("name", 50)
    val birthday = varchar("birthday", 50)
}

class Profile(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Profile>(Persons)
    var name by Persons.name
    var birthday by Persons.birthday
}
