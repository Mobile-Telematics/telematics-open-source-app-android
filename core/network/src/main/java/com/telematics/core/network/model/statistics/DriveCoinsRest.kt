package com.telematics.core.network.model.statistics

import com.google.gson.annotations.SerializedName

data class DriveCoinsRest(
    @SerializedName("TotalCoins") val totalCoins: Int
)