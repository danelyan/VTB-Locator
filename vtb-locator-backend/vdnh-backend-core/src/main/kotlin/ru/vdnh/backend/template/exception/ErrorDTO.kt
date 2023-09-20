package ru.vdnh.backend.template.exception

import kotlinx.serialization.Serializable
import ru.vdnh.backend.template.json.UUIDSerializer
import java.util.UUID

/**
 * Intended to be the result of an erroneous http call.
 */
@Serializable
data class ErrorDTO(
    val httpStatusCode: Int,
    val messageCode: String? = null,
    val title: String? = null,
    val errors: List<ErrorDefinition> = listOf()
) {
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()

    override fun toString(): String {
        return "ErrorDTO(id=$id, httpStatusCode=$httpStatusCode, messageCode=$messageCode, title=$title, errors=$errors)"
    }
}

@Serializable
data class ErrorDefinition(val errorCode: String, val field: String?, val args: Map<String, String>) {
    override fun toString(): String {
        return "ErrorDefinition(errorCode='$errorCode', field=$field, args=$args)"
    }
}
