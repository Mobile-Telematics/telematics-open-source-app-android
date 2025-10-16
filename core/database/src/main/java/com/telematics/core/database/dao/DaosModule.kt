package com.telematics.core.database.dao

import com.telematics.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    @Singleton
    fun provideLogEventDao(
        database: AppDatabase,
    ): LogEventDao = database.logEventDao

    @Provides
    @Singleton
    fun provideEcoScoreDao(
        appDatabase: AppDatabase
    ): EcoScoreDao = appDatabase.ecoScoreDao

    @Provides
    @Singleton
    fun provideScoreDao(
        appDatabase: AppDatabase
    ): ScoreDao = appDatabase.scoreDao

    @Provides
    @Singleton
    fun provideDailyScoreDao(
        appDatabase: AppDatabase
    ): DailyScoreDao = appDatabase.dailyScoreDao

    @Provides
    @Singleton
    fun provideStatisticsDao(
        appDatabase: AppDatabase
    ): StatisticsDao = appDatabase.statisticsDao

    @Provides
    @Singleton
    fun provideTripDao(
        appDatabase: AppDatabase
    ): TripDao = appDatabase.tripDao

    @Provides
    @Singleton
    fun provideVideoDataDao(
        appDatabase: AppDatabase
    ): VideoDataDao = appDatabase.videoDataDao

    @Provides
    @Singleton
    fun provideLeaderboardStatisticsDao(
        appDatabase: AppDatabase
    ): LeaderboardDataDao = appDatabase.leaderboardDataDao

    @Provides
    @Singleton
    fun provideDriveCoinsDao(
        appDatabase: AppDatabase
    ): DriveCoinsDao = appDatabase.driveCoinsDao
}