package com.telematics.core.network.model.response

import com.google.gson.annotations.SerializedName

data class CheckUserByEmailResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)