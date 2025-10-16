package com.telematics.core.network.api

import com.telematics.core.network.model.rest.ApiResponse
import com.telematics.core.network.model.statistics.LeaderboardResponse
import com.telematics.core.network.model.statistics.LeaderboardUserResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LeaderboardApi {

    @GET("v1/Leaderboard")
    suspend fun getLeaderBoard(
        @Header("DeviceToken") deviceToken: String,
        @Query("usersCount") userCount: Int? = 10,
        @Query("roundUsersCount") roundUsersCount: Int? = 2,
        @Query("ScoringRate") scoringRate: Int
    ): ApiResponse<LeaderboardResponse>

    @GET("v1/Leaderboard/user")
    suspend fun getUserLeaderboard(
        @Header("DeviceToken") deviceToken: String
    ): ApiResponse<LeaderboardUserResponse?>
}