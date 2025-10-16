package com.telematics.core.network.model.rest

import com.google.gson.annotations.SerializedName

data class ErrorData(
    @SerializedName("Key")
    val key: String?,
    @SerializedName("Message")
    val message: String?
)