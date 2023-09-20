package ru.vdnh.backend.template.tests.core.service

import ru.vdnh.backend.template.database.DatabaseConnection
import ru.vdnh.backend.template.pagination.PageRequest
import ru.vdnh.backend.template.tests.core.model.Profile
import ru.vdnh.backend.template.tests.core.persistance.ProfileRepository

class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val dbc: DatabaseConnection
) : ProfileService {
    override suspend fun add(profile: Profile): Profile {
        return dbc.suspendedQuery { profileRepository.save(profile) }
    }

    override suspend fun update(profile: Profile): Profile {
        return dbc.suspendedQuery { profileRepository.update(profile) }
    }

    override suspend fun getById(profileId: Int): Profile? {
        return dbc.suspendedQuery { profileRepository.getById(profileId) }
    }

    override suspend fun deleteById(profileId: Int) {
        dbc.suspendedQuery { profileRepository.delete(profileId) }
    }

    override suspend fun count(pageRequest: PageRequest): Int {
        return dbc.suspendedQuery { profileRepository.count(pageRequest) }
    }

    override suspend fun list(pageRequest: PageRequest): List<Profile> {
        return dbc.suspendedQuery { profileRepository.list(pageRequest) }
    }
}
