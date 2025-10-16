package com.telematics.core.network.model.request

import com.google.gson.annotations.SerializedName

data class RegisterUserByEmailRequest(
    @SerializedName("User_Profile")
    val userProfile: UserProfileRequest,
) {
    data class UserProfileRequest(
        @SerializedName("Email")
        val email: String,
        @SerializedName("Password")
        val password: String
    )
}

