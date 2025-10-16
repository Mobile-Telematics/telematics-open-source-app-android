package com.telematics.core.data.datasource.remote

import android.content.Context
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.network.api.LeaderboardApi
import com.telematics.core.network.model.statistics.LeaderboardResponse
import com.telematics.core.network.model.statistics.LeaderboardUserResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LeaderboardRemoteDataSourceImpl @Inject constructor(
    private val leaderboardApi: LeaderboardApi,
    @param:ApplicationContext val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LeaderboardRemoteDataSource {
    override suspend fun getLeaderBoard(
        deviceToken: String,
        userCount: Int?,
        roundUsersCount: Int?,
        scoringRate: Int
    ): Result<LeaderboardResponse> =
        withContext(ioDispatcher) {
            try {
                leaderboardApi.getLeaderBoard(
                    deviceToken,
                    userCount,
                    roundUsersCount,
                    scoringRate
                ).result!!.let { Result.success(it) }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getUserLeaderboard(deviceToken: String): Result<LeaderboardUserResponse?> =
        withContext(ioDispatcher) {
            try {
                leaderboardApi.getUserLeaderboard(
                    deviceToken,
                ).result!!.let { Result.success(it) }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface LeaderboardRemoteDataSource {
    suspend fun getLeaderBoard(
        deviceToken: String,
        userCount: Int?,
        roundUsersCount: Int?,
        scoringRate: Int
    ): Result<LeaderboardResponse>

    suspend fun getUserLeaderboard(
        deviceToken: String
    ): Result<LeaderboardUserResponse?>
}