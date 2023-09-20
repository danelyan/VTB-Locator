package ru.vdnh.backend.template.tests.core.model

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import ru.vdnh.backend.template.exception.AppException
import ru.vdnh.backend.template.exception.ErrorDTO
import ru.vdnh.backend.template.tests.containers.PgSQLContainerFactory
import ru.vdnh.backend.template.tests.core.testApp
import ru.vdnh.backend.template.tests.core.utils.json.JsonSettings
import ru.vdnh.backend.template.tests.core.utils.versioning.ApiVersion
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain any`
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.koin.test.KoinTest
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class TestValidatable : KoinTest {

    companion object {
        @Container
        private val dbContainer = PgSQLContainerFactory.newInstance()
    }

    private val apiVersion = ApiVersion.Latest

    @Test
    fun `Validating a place with an invalid brand`() {
        assertThrows<AppException> {
            Place(1, "Туалет", "toilet").validate()
        }
    }

    @Test
    fun `Validating a place with a valid brand`() {
        Place(1, "Музей", "museum").validate()
    }

    private fun <R> testAppWithConfig(test: TestApplicationEngine.() -> R) {
        testApp(dbContainer.configInfo(), test)
    }
}
