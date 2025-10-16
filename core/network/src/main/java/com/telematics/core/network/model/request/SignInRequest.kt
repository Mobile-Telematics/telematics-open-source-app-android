package com.telematics.core.network.model.request

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("Email")
    val email: String,
    @SerializedName("Password")
    val password: String,
)