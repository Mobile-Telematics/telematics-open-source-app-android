package com.telematics.core.data.datasource.local

import androidx.room.withTransaction
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.database.AppDatabase
import com.telematics.core.database.dao.LeaderboardDataDao
import com.telematics.core.database.entity.LeaderboardDataEntity
import com.telematics.core.datastore.PreferenceStorage
import com.telematics.core.model.leaderboard.LeaderboardType
import com.telematics.core.model.leaderboard.LeaderboardUserItems
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
class LeaderboardLocalDataSourceImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val appDatabase: AppDatabase,
    private val leaderboardDataDao: LeaderboardDataDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LeaderboardLocalDataSource {

    private val _cachedData = MutableStateFlow<List<LeaderboardUserItems>>(listOf())

    init {
        CoroutineScope(ioDispatcher).launch {
            _cachedData.value = try {
                val userId = preferenceStorage.id
                leaderboardDataDao.getLeaderboardData(userId).let { entity ->
                    entity.map {
                        LeaderboardUserItems(
                            type = try {
                                LeaderboardType.valueOf(it.type)
                            } catch (e: Exception) {
                                LeaderboardType.Rate
                            },
                            progress = it.progress,
                            place = it.place,
                            progressMax = it.progressMax
                        )
                    }
                }
            } catch (e: Exception) {
                listOf()
            }
        }
    }

    override fun getLeaderboardDataFlow(): Flow<List<LeaderboardUserItems>> =
        _cachedData.asStateFlow()

    override suspend fun saveLeaderboardData(data: List<LeaderboardUserItems>) {
        withContext(ioDispatcher) {
            try {
                val userId = preferenceStorage.id
                appDatabase.withTransaction {
                    leaderboardDataDao.deleteLeaderboardData(userId)
                    leaderboardDataDao.insertLeaderboardData(
                        data.mapIndexed { index, item ->
                            LeaderboardDataEntity(
                                type = item.type.name,
                                progress = item.progress,
                                place = item.place,
                                progressMax = item.progressMax,
                                userId = userId,
                                position = index
                            )
                        }
                    )
                }
                _cachedData.value = data
            } catch (e: Exception) {
                null
            }
        }
    }
}

interface LeaderboardLocalDataSource {
    fun getLeaderboardDataFlow(): Flow<List<LeaderboardUserItems>>
    suspend fun saveLeaderboardData(data: List<LeaderboardUserItems>)
}