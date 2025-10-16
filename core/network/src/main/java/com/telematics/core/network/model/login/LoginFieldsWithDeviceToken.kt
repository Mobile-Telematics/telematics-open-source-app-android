package com.telematics.core.network.model.login

import com.google.gson.annotations.SerializedName

data class LoginFieldsWithDeviceToken(
    @SerializedName("DeviceToken")
    val deviceToken: String? = null
)