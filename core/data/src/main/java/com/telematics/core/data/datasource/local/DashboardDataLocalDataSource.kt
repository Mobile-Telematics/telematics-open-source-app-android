package com.telematics.core.data.datasource.local

import androidx.room.withTransaction
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.datasource.mappers.toDailyScore
import com.telematics.core.data.datasource.mappers.toDailyScoreEntity
import com.telematics.core.data.datasource.mappers.toEcoScore
import com.telematics.core.data.datasource.mappers.toEcoScoreEntity
import com.telematics.core.data.datasource.mappers.toScore
import com.telematics.core.data.datasource.mappers.toScoreEntity
import com.telematics.core.data.datasource.mappers.toTrip
import com.telematics.core.data.datasource.mappers.toTripEntity
import com.telematics.core.database.AppDatabase
import com.telematics.core.database.dao.DailyScoreDao
import com.telematics.core.database.dao.DriveCoinsDao
import com.telematics.core.database.dao.EcoScoreDao
import com.telematics.core.database.dao.LeaderboardDataDao
import com.telematics.core.database.dao.ScoreDao
import com.telematics.core.database.dao.StatisticsDao
import com.telematics.core.database.dao.TripDao
import com.telematics.core.database.dao.VideoDataDao
import com.telematics.core.database.entity.DriveCoinsEntity
import com.telematics.core.database.entity.StatisticsEntity
import com.telematics.core.database.entity.VideoDataEntity
import com.telematics.core.datastore.PreferenceStorage
import com.telematics.core.model.DashboardData
import com.telematics.core.model.leaderboard.LeaderboardType
import com.telematics.core.model.statistics.EcoScore
import com.telematics.core.model.video.VideoData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardDataLocalDataSourceImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val appDatabase: AppDatabase,
    private val ecoScoreDao: EcoScoreDao,
    private val dailyScoreDao: DailyScoreDao,
    private val scoreDao: ScoreDao,
    private val statisticsDao: StatisticsDao,
    private val videoDataDao: VideoDataDao,
    private val tripDao: TripDao,
    private val driveCoinsDao: DriveCoinsDao,
    private val leaderboardDataDao: LeaderboardDataDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DashboardDataLocalDataSource {

    private val _cachedData = MutableStateFlow<DashboardData?>(null)

    init {
        CoroutineScope(ioDispatcher).launch {
            _cachedData.value = try {
                val userId = preferenceStorage.id
                val userStatistics = statisticsDao.getUserStatistics(userId)
                val leaderboard = leaderboardDataDao.getLeaderboardData(userId)
                    .find { it.type == LeaderboardType.Rate.name }?.let {
                    Pair(it.place, it.progressMax)
                }
                DashboardData(
                    tripsCount = userStatistics?.tripsCount ?: 0,
                    mileageKm = userStatistics?.mileageKm ?: 0.0,
                    drivingTime = userStatistics?.drivingTime ?: 0.0,
                    place = leaderboard?.first,
                    totalDrivers = leaderboard?.second,
                    lastTrip = tripDao.getUserTrip(userId)?.toTrip(),
                    dailyScores = dailyScoreDao.getUserScores(userId).map { it.toDailyScore() },
                    scores = scoreDao.getUserScores(userId).map { it.toScore() },
                    ecoScore = ecoScoreDao.getUserEcoScore(userId)?.toEcoScore() ?: EcoScore(),
                    driveCoins = driveCoinsDao.fetch(userId)?.driveCoins,
                    videoData = videoDataDao.getVideo(userId)?.let { entity ->
                        VideoData(
                            body = entity.body,
                            imageUrl = entity.imageUrl,
                            title = entity.title,
                            videoID = entity.videoID
                        )
                    }
                )
            } catch (e: Exception) {
                null
            }
        }
    }


    override fun getUserDashboardDataFlow(): Flow<DashboardData?> =
        _cachedData.asStateFlow()

    override suspend fun saveUserDashboardData(data: DashboardData) {
        withContext(ioDispatcher) {
            try {
                val userId = preferenceStorage.id
                appDatabase.withTransaction {
                    statisticsDao.deleteUserStatistics(userId)
                    statisticsDao.insertUserStatistics(
                        StatisticsEntity(
                            tripsCount = data.tripsCount,
                            drivingTime = data.drivingTime,
                            mileageKm = data.mileageKm,
                            userId = userId
                        )
                    )

                    tripDao.deleteUserTrips(userId)
                    data.lastTrip?.also {
                        tripDao.insertUserTrip(it.toTripEntity(userId))
                    }

                    dailyScoreDao.deleteUserScores(userId)
                    dailyScoreDao.insertUserScores(
                        data.dailyScores.map {
                            it.toDailyScoreEntity(userId)
                        }
                    )

                    scoreDao.deleteUserScores(userId)
                    scoreDao.insertUserScores(
                        data.scores.mapIndexed { index, score ->
                            score.toScoreEntity(
                                userId,
                                index
                            )
                        }
                    )

                    ecoScoreDao.deleteUserEcoScores(userId)
                    ecoScoreDao.insertUserEcoScore(data.ecoScore.toEcoScoreEntity(userId))

                    driveCoinsDao.clear(userId)
                    data.driveCoins?.apply {
                        driveCoinsDao.insert(
                            DriveCoinsEntity(
                                driveCoins = this,
                                userId = userId
                            )
                        )
                    }

                    videoDataDao.deleteVideo(userId)
                    data.videoData?.apply {
                        videoDataDao.insertVideo(
                            VideoDataEntity(
                                body = body,
                                imageUrl = imageUrl,
                                title = title,
                                videoID = videoID,
                                userId = userId
                            )
                        )

                    }
                }

                _cachedData.value = data
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun cacheEmptyDashboardData() {
        withContext(ioDispatcher) {
            if (_cachedData.value == null) {
                _cachedData.value = DashboardData()
            }
        }
    }


}

interface DashboardDataLocalDataSource {
    fun getUserDashboardDataFlow(): Flow<DashboardData?>
    suspend fun saveUserDashboardData(data: DashboardData)
    suspend fun cacheEmptyDashboardData()
}