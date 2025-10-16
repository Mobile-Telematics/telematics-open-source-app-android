package com.telematics.core.data.repository

import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.datasource.local.DashboardDataLocalDataSource
import com.telematics.core.data.datasource.local.LeaderboardLocalDataSource
import com.telematics.core.data.datasource.local.UserAuthLocalDataSource
import com.telematics.core.data.datasource.remote.DashboardDataRemoteDataSource
import com.telematics.core.data.datasource.remote.VideoRemoteDataSource
import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.data.mappers.toDashboardData
import com.telematics.core.model.DashboardData
import com.telematics.core.model.leaderboard.LeaderboardType
import com.telematics.core.model.video.VideoData
import com.telematics.core.network.mappers.toLeadetboardUser
import com.telematics.core.network.mappers.toListOfLeaderboardUserItems
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DashboardDataRepositoryImpl @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val dashboardDataLocalDataSource: DashboardDataLocalDataSource,
    private val dashboardDataRemoteDataSource: DashboardDataRemoteDataSource,
    private val leaderboardLocalDataSource: LeaderboardLocalDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val userAuthLocalDataSource: UserAuthLocalDataSource,
    private val formatter: MeasuresFormatter,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DashboardDataRepository {

    override suspend fun refreshDashboardData(
        currentDailySafetyScore: Boolean,
        currentSafetyScore: Boolean,
        currentYearEcoScore: Boolean,
        latestTrip: Boolean,
        leaderboard: Boolean,
        driveCoins: Boolean,
        strakes: Boolean,
        video: Boolean
    ) {
        withContext(ioDispatcher) {
            try {
                val data = async {
                    dashboardDataRemoteDataSource
                        .fetchDashboardData(
                            currentDailySafetyScore = currentDailySafetyScore,
                            currentSafetyScore = currentSafetyScore,
                            currentYearEcoScore = currentYearEcoScore,
                            latestTrip = latestTrip,
                            leaderboard = leaderboard,
                            drivecoins = driveCoins
                        )
                }

                val videoData = if (video) {
                    async {
                        videoRemoteDataSource.getVideoPreview(userAuthLocalDataSource.deviceToken)
                    }
                } else {
                    null
                }

                val dataResult = data.await()
                val videoDataResult = videoData?.await()

                var dashboardData =
                    dataResult
                        .getOrThrow()
                        .dashboardData!!
                        .toDashboardData(formatter)

                videoDataResult?.apply {
                    dashboardData = dashboardData
                        .copy(
                            videoData = this.getOrNull()?.let { videoDataResponse ->
                                VideoData(
                                    body = videoDataResponse.body ?: "",
                                    videoID = videoDataResponse.videoID ?: "",
                                    imageUrl = videoDataResponse.img ?: "",
                                    title = videoDataResponse.title ?: ""
                                )
                            }
                        )
                }

                if (leaderboard) {
                    try {
                        val leaderboardUserItems = dataResult
                            .getOrThrow()
                            .dashboardData!!.leaderboard!!
                            .toLeadetboardUser()
                            .toListOfLeaderboardUserItems()

                        leaderboardUserItems.find { it.type == LeaderboardType.Rate }?.apply {
                            dashboardData = dashboardData
                                .copy(
                                    place = this.place,
                                    totalDrivers = this.progressMax
                                )
                        }

                        leaderboardLocalDataSource.saveLeaderboardData(leaderboardUserItems)
                    } catch (_: Exception) {

                    }
                }

                dashboardDataLocalDataSource.saveUserDashboardData(dashboardData)

            } catch (_: Exception) {
                dashboardDataLocalDataSource.cacheEmptyDashboardData()
            }
        }
    }

    override fun getDashboardDataFlow(): Flow<DashboardData?> =
        dashboardDataLocalDataSource.getUserDashboardDataFlow()
}

interface DashboardDataRepository {
    suspend fun refreshDashboardData(
        currentDailySafetyScore: Boolean,
        currentSafetyScore: Boolean,
        currentYearEcoScore: Boolean,
        latestTrip: Boolean,
        leaderboard: Boolean,
        driveCoins: Boolean,
        strakes: Boolean,
        video: Boolean
    )

    fun getDashboardDataFlow(): Flow<DashboardData?>
}