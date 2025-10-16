package com.telematics.core.network.model.request

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("user_email")
    val email: String,
)