package ru.vdnh.backend.template.tests.core.httphandler

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import ru.vdnh.backend.template.tests.core.model.Const
import ru.vdnh.backend.template.tests.core.model.Expr
import ru.vdnh.backend.template.tests.core.model.NotANumber
import ru.vdnh.backend.template.tests.core.model.Sum
import ru.vdnh.backend.template.tests.core.model.TestInstantLongSerialization
import ru.vdnh.backend.template.tests.core.model.TestInstantStringSerialization
import ru.vdnh.backend.template.tests.core.model.TestSealedClass
import java.time.Instant

fun Route.testSerializationRoutes() {

    get("/sealed") {
        val expr: Expr = when (call.request.queryParameters["type"]) {
            "const" -> Const(call.request.queryParameters["value"]!!.toDouble())
            "sum" -> Sum(
                Const(call.request.queryParameters["value"]!!.toDouble()),
                Const(call.request.queryParameters["value"]!!.toDouble())
            )
            else -> NotANumber
        }

        call.respond(TestSealedClass(expr))
    }

    get("/instant") {
        val type = call.request.queryParameters["type"]
        requireNotNull(type)

        when (type) {
            "string" -> {
                val time = Instant.parse(call.request.queryParameters["time"]) ?: Instant.now()
                call.respond(TestInstantStringSerialization(time))
            }
            "long" -> {
                val time = Instant.ofEpochMilli(call.request.queryParameters["time"]?.toLong() ?: Instant.now().toEpochMilli())
                call.respond(TestInstantLongSerialization(time))
            }
            else -> {
                val time = Instant.now()
                call.respond(TestInstantStringSerialization(time))
            }
        }
    }
}
