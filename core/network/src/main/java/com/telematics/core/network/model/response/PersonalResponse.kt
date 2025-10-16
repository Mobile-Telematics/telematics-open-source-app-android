package com.telematics.core.network.model.response

import com.google.gson.annotations.SerializedName
import com.telematics.core.model.UserProfile

data class PersonalResponse(
    @SerializedName("birthday")
    val birthday: String?,
    @SerializedName("clientid")
    val clientId: String?,
    @SerializedName("DateCreated")
    val dateCreated: String?,
    @SerializedName("DateUpdated")
    val dateUpdated: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("first name")
    val firstName: String?,
    @SerializedName("last name")
    val lastName: String?,
    @SerializedName("phonenumber")
    val phoneNumber: String?,
    @SerializedName("profileimage")
    val profileImage: String?
)

fun PersonalResponse.asUserProfile() =
    UserProfile(
        firstName = firstName,
        lastName = lastName,
        email = this.email,
        phoneNumber = phoneNumber,
        clientId = clientId,
        birthday = birthday,
        profileImage = profileImage,
    )