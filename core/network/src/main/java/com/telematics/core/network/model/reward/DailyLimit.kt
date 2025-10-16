package com.telematics.core.network.model.reward

import com.google.gson.annotations.SerializedName

data class DailyLimit(
    @SerializedName("DailyLimit") val dailyLimit: Int
)