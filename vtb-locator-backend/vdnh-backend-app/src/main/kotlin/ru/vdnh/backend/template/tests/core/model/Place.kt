package ru.vdnh.backend.template.tests.core.model

import kotlinx.serialization.Serializable
import ru.vdnh.backend.template.validation.Validatable
import org.valiktor.Validator
import org.valiktor.functions.hasSize
import org.valiktor.functions.isIn
import org.valiktor.functions.validateForEach

@Serializable
data class Place(
    val id: Long,
    val name: String,
    val type: String,
) :
    Validatable<Place>() {
    override fun rules(validator: Validator<Place>) {
        validator
            .validate(Place::type)
            .hasSize(3, 20)
            .isIn("food", "souvenir", "toilet", "museum")
    }
}

@Serializable
data class PlaceSaveCommand(val name: String, val type: String) :
    Validatable<PlaceSaveCommand>() {
    override fun rules(validator: Validator<PlaceSaveCommand>) {
        validator
            .validate(PlaceSaveCommand::type)
            .hasSize(3, 20)
            .isIn("food", "souvenir", "toilet", "museum")
    }
}
