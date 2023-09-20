package ru.vdnh.backend.template.tests.core.service

import ru.vdnh.backend.template.pagination.PageRequest
import ru.vdnh.backend.template.tests.core.model.Profile

interface ProfileService {
    suspend fun add(profile: Profile): Profile
    suspend fun update(profile: Profile): Profile
    suspend fun getById(profileId: Int): Profile?
    suspend fun deleteById(profileId: Int)
    suspend fun count(pageRequest: PageRequest): Int
    suspend fun list(pageRequest: PageRequest): List<Profile>
}
