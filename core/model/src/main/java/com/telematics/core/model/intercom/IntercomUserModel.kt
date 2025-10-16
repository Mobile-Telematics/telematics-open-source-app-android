package com.telematics.core.model.intercom

data class IntercomUserModel(
    val userId: String,
    val userEmail: String?,
    val userName: String?,
    val phone: String?
) {
    companion object {
        fun getEmptyModel(): IntercomUserModel =
            IntercomUserModel(
                "", null, null, null
            )
    }
}
