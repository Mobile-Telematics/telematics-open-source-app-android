package com.telematics.core.network.model.settings

import com.google.gson.annotations.SerializedName

data class VideoUrlResponse(
    @SerializedName("video_url")
    val videoURL: String?
)
