package com.telematics.core.network.model.rest

import com.google.gson.annotations.SerializedName

data class ApiResult(
    @SerializedName("AccessToken")
    val accessToken: com.telematics.core.network.model.rest.AccessToken,
    @SerializedName("DeviceToken")
    val deviceToken: String,
    @SerializedName("RefreshToken")
    val refreshToken: String
)