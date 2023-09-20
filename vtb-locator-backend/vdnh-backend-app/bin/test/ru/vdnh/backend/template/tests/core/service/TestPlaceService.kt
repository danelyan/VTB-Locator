package ru.vdnh.backend.template.tests.core.service

import kotlinx.coroutines.runBlocking
import ru.vdnh.backend.template.database.DatabaseConnection
import ru.vdnh.backend.template.tests.conf.EnvironmentConfigurator
import ru.vdnh.backend.template.tests.containers.PgSQLContainerFactory
import ru.vdnh.backend.template.tests.core.model.Place
import ru.vdnh.backend.template.tests.core.model.PlaceSaveCommand
import ru.vdnh.backend.template.tests.core.model.Event
import ru.vdnh.backend.template.tests.core.model.RegisterPartReplacementCommand
import ru.vdnh.backend.template.tests.core.persistance.PlaceRepository
import ru.vdnh.backend.template.tests.core.persistance.EventRepository
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestPlaceService : KoinTest {
    companion object {
        @Container
        private val dbContainer = PgSQLContainerFactory.newInstance()
    }

    private val placeRepository: PlaceRepository by inject()
    private val eventRepository: EventRepository by inject()
    private val placesService: PlacesService by inject()
    private val dbc: DatabaseConnection by inject()

    @BeforeAll
    fun setup() {
        val appModules = EnvironmentConfigurator(dbContainer.configInfo()).getDependencyInjectionModules()
        startKoin { modules(appModules) }
    }

    @AfterEach
    fun cleanDatabase() {
        dbc.query {
            val places = placeRepository.list()
            places.forEach {
                placeRepository.delete(it.id)
            }
            placeRepository.count() `should be equal to` 0

            val events = eventRepository.list()
            events.forEach {
                eventRepository.delete(it.eventNo)
            }
            eventRepository.count() `should be equal to` 0
        }
    }

    @AfterAll
    fun close() {
        stopKoin()
    }

    @Test
    fun `Parts can be added to a place`(): Unit = runBlocking {
        // Given: a place
        val place = createCar("Mercedes-Benz", "A 180")
        val oldPartsCount = countParts()

        // And: a set of events one of which has a duplicate event number
        val eventReplacement = RegisterPartReplacementCommand(
            placeId = place.id,
            events = listOf(
                Event(eventNo = 1L, manufacturer = "Bosch", description = "Spark plug"),
                Event(eventNo = 2L, manufacturer = "Wurth", description = "Air conditioner filter"),
            )
        )

        // When: a events replacement is registered it fails
        placesService.registerPartReplacement(eventReplacement)

        // Expect: no events have been associated with the place
        countParts() `should be equal to` oldPartsCount + eventReplacement.events.size
    }

    @Test
    fun `Test that nested transactions rollback as expected`(): Unit = runBlocking {
        // Given: a place
        val place = createCar("Opel", "Corsa")
        val oldPartsCount = countParts()

        // And: a set of events one of which has a duplicate event number
        val eventReplacement = RegisterPartReplacementCommand(
            placeId = place.id,
            events = listOf(
                Event(eventNo = 1L, manufacturer = "Bosch", description = "Spark plug"),
                Event(eventNo = 2L, manufacturer = "Wurth", description = "Air conditioner filter"),
                Event(eventNo = 1L, manufacturer = "Bosch", description = "Spark plug") // note the duplicate event
            )
        )

        // When: a events replacement is registered it fails
        assertThrows<Exception> {
            placesService.registerPartReplacement(eventReplacement)
        }

        // Expect: no events have been associated with the place
        countParts() `should be equal to` oldPartsCount
    }

    private fun createCar(brand: String, model: String): Place = dbc.query {
        placeRepository.save(PlaceSaveCommand(brand, model))
    }

    private fun countParts(): Int = dbc.query { eventRepository.count() }
}
