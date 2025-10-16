package com.telematics.core.data.datasource.remote

import android.content.Context
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.network.api.UserStatisticsApi
import com.telematics.core.network.model.reward.StreaksRest
import com.telematics.core.network.model.statistics.DrivingDetailsRest
import com.telematics.core.network.model.statistics.EcoScoringRest
import com.telematics.core.network.model.statistics.UserStatisticsIndividualRest
import com.telematics.core.network.model.statistics.UserStatisticsScoreRest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserStatisticsRemoteDataSourceImpl @Inject constructor(
    private val userStatisticsApi: UserStatisticsApi,
    @param:ApplicationContext val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserStatisticsRemoteDataSource {
    override suspend fun getUserStatisticsIndividualData(
        startDate: String,
        endDate: String
    ): Result<UserStatisticsIndividualRest> =
        withContext(ioDispatcher) {
            try {
                userStatisticsApi.getIndividualData(
                    startDate,
                    endDate
                ).result!!.let { Result.success(it) }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getScoreData(
        deviceToken: String,
        startDate: String,
        endDate: String
    ): Result<UserStatisticsScoreRest> =
        withContext(ioDispatcher) {
            try {
                userStatisticsApi.getScoreData(
                    content_type = deviceToken,
                    startDate,
                    endDate
                ).result!!.let { Result.success(it) }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getDrivingDetails(
        startDate: String,
        endDate: String
    ): Result<List<DrivingDetailsRest>> =
        withContext(ioDispatcher) {
            try {
                userStatisticsApi.getDrivingDetails(
                    startDate,
                    endDate
                ).result!!.let { Result.success(it) }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getMainEcoScoring(
        startDate: String,
        endDate: String
    ): Result<EcoScoringRest> =
        withContext(ioDispatcher) {
            try {
                userStatisticsApi.getMainEcoScoring(
                    startDate,
                    endDate
                ).result!!.let { Result.success(it) }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getIndividualData(
        startDate: String,
        endDate: String
    ): Result<UserStatisticsIndividualRest> =
        withContext(ioDispatcher) {
            try {
                userStatisticsApi.getIndividualData(
                    startDate,
                    endDate
                ).result!!.let { Result.success(it) }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getStreaks(): Result<StreaksRest> =
        withContext(ioDispatcher) {
            try {
                userStatisticsApi.getStreaks().result!!.let { Result.success(it) }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface UserStatisticsRemoteDataSource {
    suspend fun getUserStatisticsIndividualData(
        startDate: String,
        endDate: String
    ): Result<UserStatisticsIndividualRest>

    suspend fun getScoreData(
        deviceToken: String,
        startDate: String,
        endDate: String
    ): Result<UserStatisticsScoreRest>

    suspend fun getDrivingDetails(
        startDate: String,
        endDate: String
    ): Result<List<DrivingDetailsRest>>

    suspend fun getMainEcoScoring(
        startDate: String,
        endDate: String
    ): Result<EcoScoringRest>

    suspend fun getIndividualData(
        startDate: String,
        endDate: String
    ): Result<UserStatisticsIndividualRest>

    suspend fun getStreaks(): Result<StreaksRest>
}