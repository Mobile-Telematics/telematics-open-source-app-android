package com.telematics.core.network.model.carservice

import com.google.gson.annotations.SerializedName

data class VehicleResponse(
    @SerializedName("Cars") val cars: List<com.telematics.core.network.model.carservice.CarRest>
)