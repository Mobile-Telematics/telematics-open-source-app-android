package com.telematics.core.data.repository.di

import com.telematics.core.data.repository.CarServiceRepository
import com.telematics.core.data.repository.CarServiceRepositoryImpl
import com.telematics.core.data.repository.DashboardDataRepository
import com.telematics.core.data.repository.DashboardDataRepositoryImpl
import com.telematics.core.data.repository.IntercomRepository
import com.telematics.core.data.repository.IntercomRepositoryImpl
import com.telematics.core.data.repository.LeaderboardRepository
import com.telematics.core.data.repository.LeaderboardRepositoryImpl
import com.telematics.core.data.repository.LogbookRepository
import com.telematics.core.data.repository.LogbookRepositoryImpl
import com.telematics.core.data.repository.OnboardingRepository
import com.telematics.core.data.repository.OnboardingRepositoryImpl
import com.telematics.core.data.repository.RewardRepository
import com.telematics.core.data.repository.RewardRepositoryImpl
import com.telematics.core.data.repository.SessionRepository
import com.telematics.core.data.repository.SessionRepositoryImpl
import com.telematics.core.data.repository.SettingsRepository
import com.telematics.core.data.repository.SettingsRepositoryImpl
import com.telematics.core.data.repository.StatisticsRepository
import com.telematics.core.data.repository.StatisticsRepositoryImpl
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.data.repository.TrackingRepositoryImpl
import com.telematics.core.data.repository.UserAuthRepository
import com.telematics.core.data.repository.UserAuthRepositoryImpl
import com.telematics.core.data.repository.UserDataRepository
import com.telematics.core.data.repository.UserDataRepositoryImpl
import com.telematics.core.data.repository.UserProfileRepository
import com.telematics.core.data.repository.UserProfileRepositoryImpl
import com.telematics.core.data.repository.UserServiceRepository
import com.telematics.core.data.repository.UserServiceRepositoryImpl
import com.telematics.core.data.repository.VideoRepository
import com.telematics.core.data.repository.VideoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {


    @Binds
    fun bindSessionRepository(repo: SessionRepositoryImpl): SessionRepository


    @Binds
    fun bindStatisticRepository(repo: StatisticsRepositoryImpl): StatisticsRepository

    @Binds
    fun bindLeaderboardRepository(repo: LeaderboardRepositoryImpl): LeaderboardRepository

    @Binds
    fun bindRewardRepository(repo: RewardRepositoryImpl): RewardRepository

    @Binds
    @Singleton
    fun bindCarServiceRepository(repo: CarServiceRepositoryImpl): CarServiceRepository

    @Binds
    fun bindSettingsRepository(repo: SettingsRepositoryImpl): SettingsRepository

    @Binds
    fun bindUserAuthRepository(repo: UserAuthRepositoryImpl): UserAuthRepository

    @Binds
    fun bindUseProfileRepository(repo: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    fun bindTrackingRepository(repo: TrackingRepositoryImpl): TrackingRepository

    @Binds
    fun bindOnboardingRepository(repo: OnboardingRepositoryImpl): OnboardingRepository

    @Binds
    fun bindUserServiceRepository(repo: UserServiceRepositoryImpl): UserServiceRepository

    @Binds
    fun bindUserDataRepository(repo: UserDataRepositoryImpl): UserDataRepository

    @Binds
    fun bindIntercomRepository(repo: IntercomRepositoryImpl): IntercomRepository

    @Binds
    fun bindDashboardRepository(repo: DashboardDataRepositoryImpl): DashboardDataRepository

    @Binds
    fun bindVideoRepository(repo: VideoRepositoryImpl): VideoRepository

    @Binds
    fun bindLogbookRepository(repo: LogbookRepositoryImpl): LogbookRepository

}