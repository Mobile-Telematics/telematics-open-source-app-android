package com.telematics.core.data.datasource.remote

import android.content.Context
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.network.api.OpenSourceApi
import com.telematics.core.network.util.createResult
import com.telematics.core.network.model.request.DashboardRequest
import com.telematics.core.network.model.response.DashboardResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DashboardDataRemoteDataSourceImpl @Inject constructor(
    private val openSourceApi: OpenSourceApi,
    @param:ApplicationContext val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DashboardDataRemoteDataSource {

    override suspend fun fetchDashboardData(
        currentDailySafetyScore: Boolean,
        currentSafetyScore: Boolean,
        currentYearEcoScore: Boolean,
        currentYearStatistics: Boolean,
        lastMonthStatistics: Boolean,
        lastWeekStatistics: Boolean,
        lastYearStatistics: Boolean,
        latestTrip: Boolean,
        leaderboard: Boolean,
        drivecoins: Boolean
    ): Result<DashboardResponse> =
        withContext(ioDispatcher) {
            try {
                openSourceApi.fetchDashboardData(
                    model = DashboardRequest(
                        DashboardRequest.Services(
                            currentDailySafetyScore = currentDailySafetyScore,
                            currentSafetyScore = currentSafetyScore,
                            currentYearEcoScore = currentYearEcoScore,
                            currentYearStatistics = currentYearStatistics,
                            lastMonthStatistics = lastMonthStatistics,
                            lastWeekStatistics = lastWeekStatistics,
                            lastYearStatistics = lastYearStatistics,
                            latestTrip = latestTrip,
                            leaderboard = leaderboard,
                            drivecoins = drivecoins
                        )
                    )
                ).createResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface DashboardDataRemoteDataSource {
    suspend fun fetchDashboardData(
        currentDailySafetyScore: Boolean,
        currentSafetyScore: Boolean,
        currentYearEcoScore: Boolean,
        currentYearStatistics: Boolean = true,
        lastMonthStatistics: Boolean = false,
        lastWeekStatistics: Boolean = false,
        lastYearStatistics: Boolean = false,
        latestTrip: Boolean,
        leaderboard: Boolean,
        drivecoins: Boolean
    ): Result<DashboardResponse>
}