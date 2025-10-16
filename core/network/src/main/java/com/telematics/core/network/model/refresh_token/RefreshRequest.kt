package com.telematics.core.network.model.refresh_token

import com.google.gson.annotations.SerializedName

data class RefreshRequest(
    @SerializedName("AccessToken")
    val accessToken: String,
    @SerializedName("RefreshToken")
    val refreshToken: String
)