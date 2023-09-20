package ru.vtb24.mobilebanking.android.locator.domain.model

data class Branch(
    val branchId: String,
    val address: String,
    val city: String,
    val coordinates: Coordinates,
    val id: Int,
    val scheduleFl: String,
    val scheduleJurL: String,
    val shortName: String,
    val special: Special
)