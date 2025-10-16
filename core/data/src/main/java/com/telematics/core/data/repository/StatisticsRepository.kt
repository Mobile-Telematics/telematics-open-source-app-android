package com.telematics.core.data.repository

import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.common.extension.DateFormat
import com.telematics.core.common.extension.timeMillsToDisplayableString
import com.telematics.core.data.datasource.remote.DriveCoinsRemoteDataSource
import com.telematics.core.data.datasource.remote.UserStatisticsRemoteDataSource
import com.telematics.core.model.statistics.DriveCoins
import com.telematics.core.model.statistics.DrivingDetailsData
import com.telematics.core.model.statistics.StatisticEcoScoringMain
import com.telematics.core.model.statistics.StatisticEcoScoringTabData
import com.telematics.core.model.statistics.UserStatisticsIndividualData
import com.telematics.core.model.statistics.UserStatisticsScoreData
import com.telematics.core.network.mappers.toDashboardEcoScoringMain
import com.telematics.core.network.mappers.toDashboardEcoScoringTabData
import com.telematics.core.network.mappers.toDriveCoins
import com.telematics.core.network.mappers.toDrivingDetailsData
import com.telematics.core.network.mappers.toUserStatisticsIndividualData
import com.telematics.core.network.mappers.toUserStatisticsScoreData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val userStatisticsRemoteDataSource: UserStatisticsRemoteDataSource,
    private val driveCoinsRemoteDataSource: DriveCoinsRemoteDataSource,
    private val userAuthRepository: UserAuthRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : StatisticsRepository {

    override suspend fun getDriveCoins(): Result<DriveCoins> =
        withContext(ioDispatcher) {
            try {
                val dateEnd =
                    System.currentTimeMillis()
                        .timeMillsToDisplayableString(DateFormat.Iso8601DateTime())

                Result.success(
                    driveCoinsRemoteDataSource
                        .getDrivingCoins("2000-01-01T00:00:01", dateEnd)
                        .getOrThrow()
                        .toDriveCoins()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getUserStatisticsIndividualData(): Result<UserStatisticsIndividualData> =
        withContext(ioDispatcher) {
            try {

                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, 2000)
                val startDate = format.format(calendar.time)
                val endDate = format.format(Calendar.getInstance().time)

                Result.success(
                    userStatisticsRemoteDataSource
                        .getUserStatisticsIndividualData(startDate, endDate)
                        .getOrThrow()
                        .toUserStatisticsIndividualData()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getDrivingDetails(): Result<List<DrivingDetailsData>> =
        withContext(ioDispatcher) {
            try {

                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val calendar = Calendar.getInstance()
                val endDate = format.format(calendar.time)
                calendar.add(Calendar.DATE, -14)
                val startDate = format.format(calendar.time)

                Result.success(
                    userStatisticsRemoteDataSource
                        .getDrivingDetails(startDate, endDate)
                        .getOrThrow()
                        .map {
                            it.toDrivingDetailsData()
                        }
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getUserStatisticsScoreData(): Result<UserStatisticsScoreData> =
        withContext(ioDispatcher) {
            try {

                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val calendar = Calendar.getInstance()
                val endDate = format.format(calendar.time)
                val startDate = format.format(calendar.time)

                Result.success(
                    userStatisticsRemoteDataSource
                        .getScoreData(
                            userAuthRepository.getDeviceToken(),
                            startDate,
                            endDate
                        )
                        .getOrThrow()
                        .toUserStatisticsScoreData()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getMainEcoScoring(): Result<StatisticEcoScoringMain> =
        withContext(ioDispatcher) {
            try {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, 2000)
                calendar.set(Calendar.MONTH, 1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = format.format(calendar.time)
                val endDate = format.format(Calendar.getInstance().time)

                Result.success(
                    userStatisticsRemoteDataSource
                        .getMainEcoScoring(
                            startDate,
                            endDate
                        )
                        .getOrThrow()
                        .toDashboardEcoScoringMain()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getEcoScoringStatisticsData(type: Int): Result<StatisticEcoScoringTabData> =
        withContext(ioDispatcher) {
            try {
                val amount = when (type) {
                    Calendar.DAY_OF_WEEK -> -7
                    Calendar.MONTH -> -30
                    Calendar.YEAR -> -365
                    else -> 0
                }

                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, amount)
                val startDate = format.format(calendar.time)
                val endDate = format.format(Calendar.getInstance().time)

                Result.success(
                    userStatisticsRemoteDataSource
                        .getIndividualData(
                            startDate,
                            endDate
                        )
                        .getOrThrow()
                        .toDashboardEcoScoringTabData()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface StatisticsRepository {
    suspend fun getDriveCoins(): Result<DriveCoins>
    suspend fun getUserStatisticsIndividualData(): Result<UserStatisticsIndividualData>
    suspend fun getDrivingDetails(): Result<List<DrivingDetailsData>>
    suspend fun getUserStatisticsScoreData(): Result<UserStatisticsScoreData>
    suspend fun getMainEcoScoring(): Result<StatisticEcoScoringMain>
    suspend fun getEcoScoringStatisticsData(type: Int): Result<StatisticEcoScoringTabData>
}