package com.telematics.core.network.model.carservice

import com.google.gson.annotations.SerializedName

data class VehicleDevicesResponse(
    @SerializedName("Elms") val elms: List<com.telematics.core.network.model.carservice.ElmRest>?
)