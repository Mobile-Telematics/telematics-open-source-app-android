package com.telematics.core.data.repository

import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.datasource.local.LeaderboardLocalDataSource
import com.telematics.core.data.datasource.remote.LeaderboardRemoteDataSource
import com.telematics.core.model.leaderboard.LeaderboardMemberData
import com.telematics.core.model.leaderboard.LeaderboardType
import com.telematics.core.model.leaderboard.LeaderboardUserItems
import com.telematics.core.network.api.LeaderboardApi
import com.telematics.core.network.mappers.toLeaderboardData
import com.telematics.core.network.mappers.toLeadetboardUser
import com.telematics.core.network.mappers.toListOfLeaderboardUserItems
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LeaderboardRepositoryImpl @Inject constructor(
    private val leaderboardApi: LeaderboardApi,
    private val leaderboardLocalDataSource: LeaderboardLocalDataSource,
    private val leaderboardRemoteDataSource: LeaderboardRemoteDataSource,
    private val userAuthRepository: UserAuthRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LeaderboardRepository {

    override suspend fun refreshLeaderboardData(): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val data = leaderboardRemoteDataSource
                    .getUserLeaderboard(userAuthRepository.getDeviceToken())
                    .getOrThrow()
                    .toLeadetboardUser()
                    .toListOfLeaderboardUserItems()

                leaderboardLocalDataSource.saveLeaderboardData(data)

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun getLeaderboardDataFlow(): Flow<List<LeaderboardUserItems>> =
        leaderboardLocalDataSource.getLeaderboardDataFlow()

    override suspend fun getLeaderboardUserData(): Result<List<LeaderboardUserItems>> =
        withContext(ioDispatcher) {
            try {
                val deviceToken = userAuthRepository.getDeviceToken()

                Result.success(
                    leaderboardRemoteDataSource
                        .getUserLeaderboard(deviceToken)
                        .getOrThrow()
                        .toLeadetboardUser()
                        .toListOfLeaderboardUserItems()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getLeaderboardUserList(type: Int): List<LeaderboardMemberData> {
        val d = leaderboardApi.getLeaderBoard(userAuthRepository.getDeviceToken(), 10, 2, type)
        return d.result?.toLeaderboardData(type) ?: listOf()
    }

    override suspend fun getLeaderboard(type: LeaderboardType): Result<List<LeaderboardMemberData>?> =
        withContext(ioDispatcher) {
            try {
                val mappedType = when (type) {
                    LeaderboardType.Rate -> 6
                    LeaderboardType.Acceleration -> 1
                    LeaderboardType.Deceleration -> 2
                    LeaderboardType.Speeding -> 4
                    LeaderboardType.Distraction -> 3
                    LeaderboardType.Turn -> 5
                    LeaderboardType.Trips -> 8
                    LeaderboardType.Distance -> 7
                    LeaderboardType.Duration -> 9
                }

                val deviceToken = userAuthRepository.getDeviceToken()

                Result.success(
                    leaderboardRemoteDataSource
                        .getLeaderBoard(deviceToken, 10, 2, 6)
                        .getOrThrow()
                        .toLeaderboardData(mappedType)
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface LeaderboardRepository {
    suspend fun refreshLeaderboardData(): Result<Unit>
    fun getLeaderboardDataFlow(): Flow<List<LeaderboardUserItems>>

    suspend fun getLeaderboardUserData(): Result<List<LeaderboardUserItems>>
    suspend fun getLeaderboardUserList(type: Int): List<LeaderboardMemberData>
    suspend fun getLeaderboard(type: LeaderboardType): Result<List<LeaderboardMemberData>?>
}