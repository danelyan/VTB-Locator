package ru.vdnh.backend.template.tests.core.persistance

import ru.vdnh.backend.template.pagination.PageRequest
import ru.vdnh.backend.template.tests.core.model.Profile

interface ProfileRepository {
    fun save(profile: Profile): Profile
    fun update(profile: Profile): Profile
    fun getById(id: Int): Profile?
    fun delete(id: Int)
    fun count(pageRequest: PageRequest = PageRequest()): Int
    fun list(pageRequest: PageRequest = PageRequest()): List<Profile>
}
