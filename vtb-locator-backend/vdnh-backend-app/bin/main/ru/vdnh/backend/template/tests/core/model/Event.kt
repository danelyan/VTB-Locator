package ru.vdnh.backend.template.tests.core.model

data class Event(val eventNo: Long, val manufacturer: String, val description: String)

data class RegisterPartReplacementCommand(val placeId: Long, val events: List<Event>)
