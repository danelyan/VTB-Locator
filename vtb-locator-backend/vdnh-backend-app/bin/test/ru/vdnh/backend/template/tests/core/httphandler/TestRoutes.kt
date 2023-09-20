package ru.vdnh.backend.template.tests.core.httphandler

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import kotlinx.coroutines.runBlocking
import ru.vdnh.backend.template.database.DatabaseConnection
import ru.vdnh.backend.template.pagination.PageRequest
import ru.vdnh.backend.template.pagination.PageResponse
import ru.vdnh.backend.template.pagination.PaginationUtils
import ru.vdnh.backend.template.tests.containers.PgSQLContainerFactory
import ru.vdnh.backend.template.tests.core.model.Place
import ru.vdnh.backend.template.tests.core.model.PlaceSaveCommand
import ru.vdnh.backend.template.tests.core.persistance.PlaceRepository
import ru.vdnh.backend.template.tests.core.testApp
import ru.vdnh.backend.template.tests.core.utils.json.JsonSettings
import ru.vdnh.backend.template.tests.core.utils.versioning.ApiVersion
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be greater than`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.test.KoinTest
import org.koin.test.inject
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestRoutes : KoinTest {
    companion object {
        @Container
        private val dbContainer = PgSQLContainerFactory.newInstance()
    }

    private val apiVersion = ApiVersion.Latest
    private val placeRepository: PlaceRepository by inject()
    private val dbc: DatabaseConnection by inject()

    @AfterEach
    fun cleanDatabase() {
        dbc.query {
            val places =
                placeRepository.list(PageRequest(page = 0, size = Int.MAX_VALUE, sort = listOf(), filter = listOf()))
            places.forEach {
                placeRepository.delete(it.id)
            }
            placeRepository.count() `should be equal to` 0
        }
    }

    @Test
    fun `Fetching a place that does not exists returns a 404 Not Found`() = testAppWithConfig {
        with(handleRequest(HttpMethod.Get, "/$apiVersion/places/12345")) {
            response.status() `should be equal to` HttpStatusCode.NotFound
        }
    }

    @Test
    fun `Fetching a place that exists returns correctly`() = testAppWithConfig {
        val newCar = insertCar()

        with(handleRequest(HttpMethod.Get, "/$apiVersion/places/${newCar.id}")) {
            response.status() `should be equal to` HttpStatusCode.OK
            val place: Place = JsonSettings.fromJson(response.content)
            place.id `should be equal to` newCar.id
            place.name `should be equal to` newCar.name
            place.type `should be equal to` newCar.type
        }
    }

    @Test
    fun `Creating a new place returns correctly`() = testAppWithConfig {
        val cmd = PlaceSaveCommand("porsche", "spyder")

        with(
            handleRequest(HttpMethod.Post, "/$apiVersion/places") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(JsonSettings.toJson(cmd))
            }
        ) {
            response.status() `should be equal to` HttpStatusCode.OK
            val place: Place = JsonSettings.fromJson(response.content)
            place.id `should be greater than` 0
            place.name `should be equal to` cmd.brand
            place.type `should be equal to` cmd.model
            countCars() `should be equal to` 1
        }
    }

    @Test
    fun `Updating a place correctly`() = testAppWithConfig {
        val cmd = PlaceSaveCommand("porsche", "spyder")

        val newCar = with(
            handleRequest(HttpMethod.Post, "/$apiVersion/places") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(JsonSettings.toJson(cmd))
            }
        ) {
            response.status() `should be equal to` HttpStatusCode.OK
            val place: Place = JsonSettings.fromJson(response.content)
            place.id `should be greater than` 0
            place.name `should be equal to` cmd.brand
            place.type `should be equal to` cmd.model
            countCars() `should be equal to` 1
            place
        }

        val updatedCar = PlaceSaveCommand(newCar.name, newCar.type + "_2")
        with(
            handleRequest(HttpMethod.Put, "/$apiVersion/places/${newCar.id}") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(JsonSettings.toJson(updatedCar))
            }
        ) {
            response.status() `should be equal to` HttpStatusCode.OK
            val place: Place = JsonSettings.fromJson(response.content)
            place.id `should be equal to` newCar.id
            place.name `should be equal to` updatedCar.brand
            place.type `should be equal to` updatedCar.model
            countCars() `should be equal to` 1
        }
    }

    @Nested
    @DisplayName("Test place listing: filtering and ordering")
    inner class TestPlaceList {

        @Test
        fun `The list endpoint works without any extra parameters`() = testAppWithConfig {
            val expectedCars = generateCars(5)
            with(handleRequest(HttpMethod.Get, "/$apiVersion/places")) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data.size `should be equal to` expectedCars.size
                res.data `should be equal to` expectedCars
            }
        }

        @Test
        fun `Test that the first page of the list has the appropriate parameters`() = testAppWithConfig {
            val expectedCars = generateCars(5)
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=3"
                )
            ) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data.size `should be equal to` 3
                res.data `should be equal to` expectedCars.subList(0, 3)

                // Verify pagination info
                res.meta.page `should be equal to` 0
                res.meta.size `should be equal to` 3
                res.meta.totalElements `should be equal to` expectedCars.size
                res.meta.totalPages `should be equal to` 2
                res.meta.first `should be equal to` true
                res.meta.last `should be equal to` false
                res.links.self `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=3"
                res.links.first `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=3"
                res.links.prev `should be equal to` null
                res.links.next `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=1&${PaginationUtils.PAGE_SIZE}=3"
                res.links.last `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=1&${PaginationUtils.PAGE_SIZE}=3"
            }
        }

        @Test
        fun `Test that the second page of the list has the appropriate parameters`() = testAppWithConfig {
            val expectedCars = generateCars(5)
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=1&${PaginationUtils.PAGE_SIZE}=3"
                )
            ) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data.size `should be equal to` 2
                res.data `should be equal to` expectedCars.subList(3, 5)

                // Verify pagination info
                res.meta.page `should be equal to` 1
                res.meta.size `should be equal to` 3
                res.meta.totalElements `should be equal to` expectedCars.size
                res.meta.totalPages `should be equal to` 2
                res.meta.first `should be equal to` false
                res.meta.last `should be equal to` true
                res.links.self `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=1&${PaginationUtils.PAGE_SIZE}=3"
                res.links.first `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=3"
                res.links.prev `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=3"
                res.links.next `should be equal to` null
                res.links.last `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=1&${PaginationUtils.PAGE_SIZE}=3"
            }
        }

        @Test
        fun `Requesting a non-existing page is handled gracefully`() = testAppWithConfig {
            generateCars(5)
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=2&${PaginationUtils.PAGE_SIZE}=5"
                )
            ) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data.size `should be equal to` 0

                // Verify pagination info
                res.meta.first `should be equal to` false
                res.meta.last `should be equal to` false
                res.links.self `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=2&${PaginationUtils.PAGE_SIZE}=5"
                res.links.first `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=5"
                res.links.prev `should be equal to` null
                res.links.next `should be equal to` null
                res.links.last `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=5"
            }
        }

        @Test
        fun `Results are correctly sorted in ascending order`() = testAppWithConfig {
            val expectedCars = generateCars(5)
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_SORT}[id]=asc"
                )
            ) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data `should be equal to` expectedCars.sortedBy { it.id }

                // Verify pagination info
                res.links.self `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_SORT}[id]=asc"
            }
        }

        @Test
        fun `Results are correctly sorted in descending order`() = testAppWithConfig {
            val expectedCars = generateCars(5)
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_SORT}[id]=desc"
                )
            ) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data `should be equal to` expectedCars.sortedByDescending { it.id }

                // Verify pagination info
                res.links.self `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_SORT}[id]=desc"
            }
        }

        @Test
        fun `Results are correctly sorted by multiple parameters`() = testAppWithConfig {
            val expectedCars = generateCars(5)
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_SORT}[brand]=asc&${PaginationUtils.PAGE_SORT}[model]=desc"
                )
            ) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data `should be equal to` expectedCars.sortedWith(compareBy<Place> { it.name }.thenByDescending { it.type })

                // Verify pagination info
                res.links.self `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_SORT}[brand]=asc&${PaginationUtils.PAGE_SORT}[model]=desc"
            }
        }

        @Test
        fun `Results can be filtered`() = testAppWithConfig {
            val expectedCars = generateCars(5)
            val targetCar = expectedCars.random()
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_FILTER}[brand]=${targetCar.name}&${PaginationUtils.PAGE_FILTER}[model]=${targetCar.type}"
                )
            ) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data `should be equal to` expectedCars.filter { it.name == targetCar.name && it.type == targetCar.type }

                // Verify pagination info
                res.links.self `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_FILTER}[brand]=${targetCar.name}&${PaginationUtils.PAGE_FILTER}[model]=${targetCar.type}"
            }
        }

        @Test
        fun `Individual filters may reference more than one value`() = testAppWithConfig {
            val expectedCars = generateCars(5)
            val ids = listOf(expectedCars.first().id, expectedCars.last().id)
            val queryParams = ids.joinToString(",")
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_FILTER}[id]=$queryParams"
                )
            ) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data `should be equal to` expectedCars.filter { it.id in ids }

                // Verify pagination info
                res.links.self `should be equal to` "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_FILTER}[id]=$queryParams"
            }
        }

        @Test
        fun `Results are empty when the filters do not match the data`() = testAppWithConfig {
            generateCars(5)
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/$apiVersion/places?${PaginationUtils.PAGE_NUMBER}=0&${PaginationUtils.PAGE_SIZE}=10&${PaginationUtils.PAGE_FILTER}[brand]=brand000"
                )
            ) {
                response.status() `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Place> = JsonSettings.fromJson(response.content)
                res.data `should be equal to` listOf()
            }
        }
    }

    private fun generateCars(n: Int): List<Place> {
        val result: MutableList<Place> = mutableListOf()
        repeat(n) {
            val id = UUID.randomUUID()
            result.add(insertCar(brand = "BRAND: $id", model = "MODEL: $id"))
        }
        return result
    }

    private fun insertCar(
        brand: String = UUID.randomUUID().toString(),
        model: String = UUID.randomUUID().toString()
    ): Place {
        return dbc.query {
            val newCar = PlaceSaveCommand(brand, model)
            placeRepository.save(newCar)
        }
    }

    private fun countCars(): Int {
        return dbc.query { placeRepository.count() }
    }

    private fun <R> testAppWithConfig(test: suspend TestApplicationEngine.() -> R) {
        testApp(dbContainer.configInfo()) {
            runBlocking { test() }
        }
    }
}
