package com.telematics.core.network.model.login

import com.google.gson.annotations.SerializedName

data class LoginWithDeviceTokenBody(
    @SerializedName("loginFields")
    val loginFields: com.telematics.core.network.model.login.LoginFieldsWithDeviceToken,
    @SerializedName("password")
    val password: String
)