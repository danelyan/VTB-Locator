package ru.vdnh.backend.template.tests.core.httphandler

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import ru.vdnh.backend.template.pagination.PageResponse
import ru.vdnh.backend.template.pagination.parsePageRequest
import ru.vdnh.backend.template.tests.core.model.Place
import ru.vdnh.backend.template.tests.core.model.PlaceSaveCommand
import ru.vdnh.backend.template.tests.core.service.PlacesService
import ru.vdnh.backend.template.tests.core.service.ProfileService
import ru.vdnh.backend.template.tests.core.utils.pagination.respondPaged
import ru.vdnh.backend.template.tests.core.utils.versioning.ApiVersion
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class DefaultRoutesInjector : KoinComponent {
    val placesService: PlacesService by inject()
    val profileService: ProfileService by inject()
}

fun Route.defaultRoutes() {
    val apiVersion = ApiVersion.Latest
    val injector = DefaultRoutesInjector()
    val placeService = injector.placesService
    val personService = injector.profileService

    get("/$apiVersion/places") {
        val pageRequest = call.parsePageRequest()
        val totalElements = placeService.count(pageRequest)
        val data = placeService.list(pageRequest)
        call.respondPaged(
            PageResponse.from(
                pageRequest = pageRequest,
                totalElements = totalElements,
                data = data,
                path = call.request.path()
            )
        )
    }

    get("/$apiVersion/places/{id}") {
        val placeId = call.parameters["id"]?.toLong() ?: -1
        when (val place = placeService.getPlaceById(placeId)) {
            null -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(place)
        }
    }

    post("/$apiVersion/places") {
        val newCar = call.receive<PlaceSaveCommand>()
        newCar.validate()

        val insertedCar = placeService.insertNewPlace(PlaceSaveCommand(newCar.name, newCar.type))
        call.respond(insertedCar)
    }

    put("/$apiVersion/places/{id}") {
        val place = call.receive<PlaceSaveCommand>()
        place.validate()
        val placeId = call.parameters["id"]?.toLong() ?: -1
        val placeToUpdate = Place(placeId, place.name, place.type)

        val updatedCar = placeService.updatePlace(placeToUpdate)
        call.respond(updatedCar)
    }

    get("/$apiVersion/persons") {
        val pageRequest = call.parsePageRequest()
        val totalElements = personService.count(pageRequest)
        val data = personService.list(pageRequest)
        call.respond(
            PageResponse.from(
                pageRequest = pageRequest,
                totalElements = totalElements,
                data = data,
                path = call.request.path()
            )
        )
    }
}
