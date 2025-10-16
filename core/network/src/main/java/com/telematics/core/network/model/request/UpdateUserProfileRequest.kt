package com.telematics.core.network.model.request

import com.google.gson.annotations.SerializedName
import com.telematics.core.model.UserProfile

data class UpdateUserProfileRequest(
    @SerializedName("personal")
    val personal: Personal?
) {
    data class Personal(
        @SerializedName("first name")
        val firstName: String?,
        @SerializedName("last name")
        val lastName: String?,
        @SerializedName("clientid")
        val clientId: String?,
        @SerializedName("birthday")
        val birthday: String?,
        @SerializedName("phonenumber")
        val phoneNumber: String?
    )
}

fun UserProfile.asUpdateUserProfileRequest() =
    UpdateUserProfileRequest(
        personal = UpdateUserProfileRequest.Personal(
            firstName = firstName,
            lastName = lastName,
            clientId = clientId,
            birthday = birthday,
            phoneNumber = phoneNumber
        )
    )