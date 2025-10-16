package com.telematics.core.network.model.response

import com.google.gson.annotations.SerializedName

data class GetUserProfileResponse(
    @SerializedName("personal")
    val personal: PersonalResponse?,
    @SerializedName("system")
    val system: SystemResponse?
)