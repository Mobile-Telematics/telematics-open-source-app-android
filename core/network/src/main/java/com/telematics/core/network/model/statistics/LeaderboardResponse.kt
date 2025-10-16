package com.telematics.core.network.model.statistics

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(
    @SerializedName("ScoringRate")
    val scoringRate: String,
    @SerializedName("Users")
    val users: List<com.telematics.core.network.model.statistics.LeaderboardUserBody>?,
    @SerializedName("UsersNumber")
    val usersNumber: Int
)