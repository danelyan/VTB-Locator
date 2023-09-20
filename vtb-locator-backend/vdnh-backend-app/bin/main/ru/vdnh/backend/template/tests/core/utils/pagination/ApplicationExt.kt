package ru.vdnh.backend.template.tests.core.utils.pagination

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import ru.vdnh.backend.template.pagination.PageResponse
import ru.vdnh.backend.template.tests.core.utils.json.JsonSettings

suspend inline fun <reified T> ApplicationCall.respondPaged(message: PageResponse<T>) {
    response.pipeline.execute(this, TextContent(JsonSettings.toJson(message), ContentType.Application.Json))
}
