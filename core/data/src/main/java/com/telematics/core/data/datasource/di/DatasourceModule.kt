@file:Suppress("DEPRECATION")

package com.telematics.core.data.datasource.di

import com.telematicssdk.tracking.TrackingApi
import com.telematics.core.data.datasource.local.DashboardDataLocalDataSource
import com.telematics.core.data.datasource.local.DashboardDataLocalDataSourceImpl
import com.telematics.core.data.datasource.local.LeaderboardLocalDataSource
import com.telematics.core.data.datasource.local.LeaderboardLocalDataSourceImpl
import com.telematics.core.data.datasource.local.OnboardingLocalDataSource
import com.telematics.core.data.datasource.local.OnboardingLocalDataSourceImpl
import com.telematics.core.data.datasource.local.TrackingLocalDataSource
import com.telematics.core.data.datasource.local.TrackingLocalDataSourceImpl
import com.telematics.core.data.datasource.local.UserAuthLocalDataSource
import com.telematics.core.data.datasource.local.UserAuthLocalDataSourceImpl
import com.telematics.core.data.datasource.local.UserDataLocalDataSource
import com.telematics.core.data.datasource.local.UserDataLocalDataSourceImpl
import com.telematics.core.data.datasource.local.UserProfileLocalDataSource
import com.telematics.core.data.datasource.local.UserProfileLocalDataSourceImpl
import com.telematics.core.data.datasource.remote.DashboardDataRemoteDataSource
import com.telematics.core.data.datasource.remote.DashboardDataRemoteDataSourceImpl
import com.telematics.core.data.datasource.remote.DriveCoinsRemoteDataSource
import com.telematics.core.data.datasource.remote.DriveCoinsRemoteDataSourceImpl
import com.telematics.core.data.datasource.remote.LeaderboardRemoteDataSource
import com.telematics.core.data.datasource.remote.LeaderboardRemoteDataSourceImpl
import com.telematics.core.data.datasource.remote.LogbookRemoteDataSource
import com.telematics.core.data.datasource.remote.LogbookRemoteDataSourceImpl
import com.telematics.core.data.datasource.remote.UserAuthRemoteDataSource
import com.telematics.core.data.datasource.remote.UserAuthRemoteDataSourceImpl
import com.telematics.core.data.datasource.remote.UserProfileRemoteDataSource
import com.telematics.core.data.datasource.remote.UserProfileRemoteDataSourceImpl
import com.telematics.core.data.datasource.remote.UserServiceRemoteDataSource
import com.telematics.core.data.datasource.remote.UserServiceRemoteDataSourceImpl
import com.telematics.core.data.datasource.remote.UserStatisticsRemoteDataSource
import com.telematics.core.data.datasource.remote.UserStatisticsRemoteDataSourceImpl
import com.telematics.core.data.datasource.remote.VideoRemoteDataSource
import com.telematics.core.data.datasource.remote.VideoRemoteDataSourceImpl
import com.telematics.core.datastore.AuthHolder
import com.telematics.core.datastore.AuthHolderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataSourceModule {
    @Provides
    @Singleton
    fun provideTrackingApi(): TrackingApi = TrackingApi.getInstance()

}

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceBinds {
    @Binds
    fun bindUserAuthRemoteDataSource(dataSource: UserAuthRemoteDataSourceImpl): UserAuthRemoteDataSource

    @Binds
    fun bindUserAuthLocalDataSource(dataSource: UserAuthLocalDataSourceImpl): UserAuthLocalDataSource

    @Binds
    fun bindUserProfileRemoteDataSource(dataSource: UserProfileRemoteDataSourceImpl): UserProfileRemoteDataSource

    @Binds
    fun bindUserProfileLocalDataSource(dataSource: UserProfileLocalDataSourceImpl): UserProfileLocalDataSource

    @Binds
    fun bindTrackingLocalDataSource(datasource: TrackingLocalDataSourceImpl): TrackingLocalDataSource

    @Binds
    fun bindOnboardingLocalDataSource(datasource: OnboardingLocalDataSourceImpl): OnboardingLocalDataSource

    @Binds
    fun bindUserStatisticRemoteDataSource(datasource: UserStatisticsRemoteDataSourceImpl): UserStatisticsRemoteDataSource

    @Binds
    fun bindDriveCoinsRemoteDataSource(datasource: DriveCoinsRemoteDataSourceImpl): DriveCoinsRemoteDataSource

    @Binds
    fun bindLeaderboardRemoteDataSource(datasource: LeaderboardRemoteDataSourceImpl): LeaderboardRemoteDataSource

    @Binds
    fun bindUserServiceRemoteDataSource(datasource: UserServiceRemoteDataSourceImpl): UserServiceRemoteDataSource

    @Binds
    fun bindUserDataLocalDataSource(datasource: UserDataLocalDataSourceImpl): UserDataLocalDataSource

    @Binds
    fun bindAuthHolder(datasource: AuthHolderImpl): AuthHolder

    @Binds
    fun bindDashboardDataRemoteDataSource(dataSource: DashboardDataRemoteDataSourceImpl): DashboardDataRemoteDataSource

    @Binds
    @Singleton
    fun bindDashboardDataLocalDataSource(dataSource: DashboardDataLocalDataSourceImpl): DashboardDataLocalDataSource

    @Binds
    fun bindVideoRemoteDataSource(datasource: VideoRemoteDataSourceImpl): VideoRemoteDataSource

    @Binds
    fun bindLeaderboardLocalDataSource(dataSource: LeaderboardLocalDataSourceImpl): LeaderboardLocalDataSource

    @Binds
    fun bindLogbookRemoteDataSource(datasource: LogbookRemoteDataSourceImpl): LogbookRemoteDataSource
}