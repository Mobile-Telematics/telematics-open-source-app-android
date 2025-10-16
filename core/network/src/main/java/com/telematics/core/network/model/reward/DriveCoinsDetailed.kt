package com.telematics.core.network.model.reward

import com.google.gson.annotations.SerializedName

data class DriveCoinsDetailed(
    @SerializedName("CoinCategoryName") val coinCategoryName: String,
    @SerializedName("CoinCategoryToken") val coinCategoryToken: String,
    @SerializedName("CoinFactor") val coinFactor: String,
    @SerializedName("CoinFactorToken") val coinFactorToken: String,
    @SerializedName("CoinsSum") val coinsSum: Int
)