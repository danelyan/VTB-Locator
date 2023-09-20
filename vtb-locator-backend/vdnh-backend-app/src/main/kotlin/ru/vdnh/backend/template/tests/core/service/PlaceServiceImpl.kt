package ru.vdnh.backend.template.tests.core.service

import ru.vdnh.backend.template.database.DatabaseConnection
import ru.vdnh.backend.template.pagination.PageRequest
import ru.vdnh.backend.template.tests.core.model.Place
import ru.vdnh.backend.template.tests.core.model.PlaceSaveCommand
import ru.vdnh.backend.template.tests.core.persistance.PlaceRepository
import ru.vdnh.backend.template.tests.core.persistance.EventRepository
import org.slf4j.LoggerFactory

class PlaceServiceImpl(
    private val placeRepository: PlaceRepository,
    private val eventRepository: EventRepository,
    private val dbc: DatabaseConnection
) :
    PlacesService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override suspend fun count(pageRequest: PageRequest): Int {
        return dbc.suspendedQuery {
            log.info("Counting places from the repository")
            placeRepository.count(pageRequest)
        }
    }

    override suspend fun getPlaceById(placeId: Long): Place? {
        return dbc.suspendedQuery { placeRepository.getById(placeId) }
    }

    override suspend fun insertNewPlace(newCar: PlaceSaveCommand): Place {
        return dbc.suspendedQuery { placeRepository.save(newCar) }
    }

    override suspend fun updatePlace(place: Place): Place {
        return dbc.suspendedQuery {
            placeRepository.update(place)
        }
    }

    override suspend fun list(pageRequest: PageRequest): List<Place> {
        return dbc.suspendedQuery {
            placeRepository.list(pageRequest)
        }
    }
}
