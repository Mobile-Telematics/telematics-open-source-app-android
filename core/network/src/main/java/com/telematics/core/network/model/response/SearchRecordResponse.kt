package com.telematics.core.network.model.response


import com.google.gson.annotations.SerializedName

data class SearchRecordResponse(
    @SerializedName("uid")
    val uid: Uid?
) {
    data class Uid(
        @SerializedName("personal")
        val personal: PersonalResponse?,
        @SerializedName("system")
        val system: SystemResponse?
    )
}