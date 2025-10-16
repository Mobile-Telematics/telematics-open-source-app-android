package com.telematics.core.network.model.response

import com.google.gson.annotations.SerializedName

data class UploadUserImageResponse(
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("message")
    val message: String?
)