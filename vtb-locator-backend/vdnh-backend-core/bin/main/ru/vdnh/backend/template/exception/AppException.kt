package ru.vdnh.backend.template.exception

class AppException(
    val code: ErrorCode,
    val title: String? = null,
    val errors: MutableList<ErrorDefinition> = mutableListOf()
) : Exception()
