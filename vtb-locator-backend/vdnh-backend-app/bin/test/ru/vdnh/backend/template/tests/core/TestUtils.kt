package ru.vdnh.backend.template.tests.core

import io.ktor.config.ApplicationConfig
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import ru.vdnh.backend.template.tests.module

fun <R> testApp(testConfig: ApplicationConfig, test: TestApplicationEngine.() -> R) {
    withTestApplication(
        {
            module(testConfig)
        },
        test
    )
}
