package com.telematics.core.network.model

import com.google.gson.annotations.SerializedName

data class ResponseError(
    @SerializedName("error")
    val message: String?,
)
