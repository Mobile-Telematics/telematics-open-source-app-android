package com.telematics.core.model

data class UserProfile(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val birthday: String?,
    val clientId: String?,
    val profileImage: String?,
) {
    val fullName = "${firstName.orEmpty()} ${lastName.orEmpty()}".trim()
}

fun getEmptyUserProfile() = UserProfile(
    firstName = null,
    lastName = null,
    email = null,
    phoneNumber = null,
    clientId = null,
    birthday = null,
    profileImage = null,
)
