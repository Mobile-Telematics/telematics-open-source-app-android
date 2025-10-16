package com.telematics.core.network.model.response


import com.google.gson.annotations.SerializedName

data class FetchOdometerDataResponse(
    @SerializedName("odometer_data")
    val odometerData: List<OdometerData>?,
    @SerializedName("vehicle_id")
    val vehicleId: Int?
) {
    data class OdometerData(
        @SerializedName("datecreated")
        val dateCreated: String?,
        @SerializedName("photo_url")
        val photoUrl: String?,
        @SerializedName("value")
        val value: Float?
    )
}
