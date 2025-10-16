package com.telematics.core.model.tracking

data class ChangeTripEvent(
    val eventType: String?,
    val latitude: Double?,
    val longitude: Double?,
    val pointDate: String?,
    val changeTypeTo: String?
)