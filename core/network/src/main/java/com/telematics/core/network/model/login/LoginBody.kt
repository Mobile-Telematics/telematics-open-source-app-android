package com.telematics.core.network.model.login

import com.google.gson.annotations.SerializedName

data class LoginBody(
    @SerializedName("loginFields")
    val loginFields: com.telematics.core.network.model.login.LoginFields,
    @SerializedName("password")
    val password: String
)