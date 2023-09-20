package ru.vdnh.backend.template.log

import ch.qos.logback.contrib.json.JsonFormatter
import ch.qos.logback.contrib.json.classic.JsonLayout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json.Default.encodeToString

/**
 * Semi-structured formatter for logs:
 * 2020-10-28T10:28:03.564Z INFO  258f7191-db2e-4cc6-8af6-627c9e8e066e Ktor server started...   {...}
 */
class SemiStructuredLogFormatter : JsonFormatter {
    companion object {
        const val REQUEST_ID_HEADER = "X-Request-Id"
    }

    // Configurable from logback.xml with <jsonSuffix>JSON:</jsonSuffix>
    var prefix = ""
    var suffix = ""
    var jsonPrefix = ""
    var jsonSuffix = ""

    @Serializable
    internal data class LogResponse(
        val timestamp: String,
        val level: String,
        val thread: String,
        val logger: String,
        val message: String,
        val exception: String? = null,
        val metadata: Map<String, String>? = null
    )

    override fun toJsonString(map: Map<*, *>): String {
        // Using internal class because kotlinx-serialization does not support Map<*, *> serialization:
        // "Star projections in type arguments are not allowed, but had Map<*, *>"
        val json = LogResponse(
            timestamp = map[JsonLayout.TIMESTAMP_ATTR_NAME]?.toString() ?: "",
            level = map[JsonLayout.LEVEL_ATTR_NAME]?.toString() ?: "",
            thread = map[JsonLayout.THREAD_ATTR_NAME]?.toString() ?: "",
            logger = map[JsonLayout.LOGGER_ATTR_NAME]?.toString() ?: "",
            message = map[JsonLayout.FORMATTED_MESSAGE_ATTR_NAME]?.toString() ?: "",
            exception = map[JsonLayout.EXCEPTION_ATTR_NAME]?.toString(),
            metadata = map[JsonLayout.MDC_ATTR_NAME]?.let {
                @Suppress("UNCHECKED_CAST")
                it as Map<String, String>
            }
        )
        val requestId = json.metadata?.get(REQUEST_ID_HEADER)

        // Construct log message
        var str = ""
        str += prefix
        str += "${json.timestamp} "
        str += "${json.level} ".padEnd(6)
        str += if (requestId != null && requestId.isNotEmpty()) "$requestId " else ""
        str += "${json.message} "
        str = str.padEnd(125) // add padding to the first fields for better readability
        str += if (json.exception != null && json.exception.isNotEmpty()) "${json.exception} " else ""
        str += jsonPrefix
        str += encodeToString(LogResponse.serializer(), json)
        str += jsonSuffix
        str += suffix
        str += "\n"

        return str
    }
}
