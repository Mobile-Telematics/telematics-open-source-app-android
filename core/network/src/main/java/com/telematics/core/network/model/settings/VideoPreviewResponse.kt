package com.telematics.core.network.model.settings

import com.google.gson.annotations.SerializedName

data class VideoPreviewResponse(
    val body: String?,
    val img: String?,
    val title: String?,
    @SerializedName("video_id")
    val videoID: String?
)