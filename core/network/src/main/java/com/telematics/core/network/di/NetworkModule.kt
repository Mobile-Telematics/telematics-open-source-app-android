package com.telematics.core.network.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.google.gson.Gson
import com.telematics.core.common.provider.ApiEventsControl
import com.telematics.core.common.provider.ApiEventsImpl
import com.telematics.core.common.provider.ApiEventsProvider
import com.telematics.core.network.BuildConfig
import com.telematics.core.network.api.AuthApi
import com.telematics.core.network.api.CarServiceApi
import com.telematics.core.network.api.DriveCoinsApi
import com.telematics.core.network.api.LeaderboardApi
import com.telematics.core.network.api.OpenSourceApi
import com.telematics.core.network.api.RefreshApi
import com.telematics.core.network.api.TripEventTypeApi
import com.telematics.core.network.api.UserServiceApi
import com.telematics.core.network.api.UserStatisticsApi
import com.telematics.core.network.interceptor.ErrorInterceptor
import com.telematics.core.network.interceptor.MainInterceptor
import com.telematics.core.network.interceptor.MockInterceptor
import com.telematics.core.network.interceptor.OfflineInterceptor
import com.telematics.core.network.interceptor.TimeoutInterceptor
import com.telematics.core.network.interceptor.UnauthErrorInterceptor
import com.telematics.core.network.interceptor.UnsupportedVersionErrorInterceptor
import com.telematics.core.network.interceptor.UserAgentInterceptor
import com.telematics.core.network.interceptor.UserProviderValuesInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    /*    @Singleton
        @Provides
        fun provideTransactionsUrl(): String {
            return BuildConfig.userServiceUrl
        }*/

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Reusable
    @Provides
    fun provideGsonConverterFactory(): Converter.Factory = GsonConverterFactory.create(Gson())

    @Singleton
    @Provides
    fun providesTimeoutInterceptor(): TimeoutInterceptor = TimeoutInterceptor(
        connectTimeoutMillis = 30_000,
        readTimeoutMillis = 30_000,
        writeTimeoutMillis = 30_000
    )

    @Provides
    fun provideConnectivityManager(
        @ApplicationContext application: Context
    ): ConnectivityManager? = application.getSystemService<ConnectivityManager>()

    @Provides
    fun provideUnauthErrorInterceptor(
        apiEventsControl: ApiEventsControl
    ): UnauthErrorInterceptor = UnauthErrorInterceptor {
        apiEventsControl.postUnauthenticatedErrorEvent()
    }

    @Provides
    fun provideUnsupportedVersionErrorInterceptor(
        apiEventsControl: ApiEventsControl
    ): UnsupportedVersionErrorInterceptor = UnsupportedVersionErrorInterceptor {
        apiEventsControl.postUnsupportedVersionErrorEvent()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        mainInterceptor: MainInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        errorInterceptor: ErrorInterceptor,
        timeoutInterceptor: TimeoutInterceptor,
        offlineInterceptor: OfflineInterceptor,
        unauthErrorInterceptor: UnauthErrorInterceptor,
        unsupportedVersionErrorInterceptor: UnsupportedVersionErrorInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(offlineInterceptor)
            addInterceptor(timeoutInterceptor)
            addInterceptor(mainInterceptor)
            authenticator(mainInterceptor)
            addInterceptor(loggingInterceptor)
            addInterceptor(errorInterceptor)
            addInterceptor(unauthErrorInterceptor)
            addInterceptor(unsupportedVersionErrorInterceptor)
            //addInterceptor(MockInterceptor())
        }.build()
    }

    @Singleton
    @Provides
    fun provideDriveCoinsApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): DriveCoinsApi {
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.DRIVE_COINS_URL)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(DriveCoinsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUserStatisticsApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): UserStatisticsApi {
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.USER_STATISTICS_URL)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(UserStatisticsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideLeaderboardApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): LeaderboardApi {
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.LEADERBOARD_URL)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(LeaderboardApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTripEventTypeApi(
        loggingInterceptor: HttpLoggingInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
        converterFactory: Converter.Factory
    ): TripEventTypeApi {

        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(userAgentInterceptor)
        }.build()

        val retrofit = Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.TRIP_EVENT_TYPE_URL)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(TripEventTypeApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRefreshApi(
        loggingInterceptor: HttpLoggingInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
        converterFactory: Converter.Factory,
    ): RefreshApi {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(userAgentInterceptor)
        }.build()
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.USER_SERVICE_URL)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(RefreshApi::class.java)
    }

    @Singleton
    @Provides
    fun UserServiceApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): UserServiceApi {
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.USER_SERVICE_URL)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(UserServiceApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCarServiceApi(
        loggingInterceptor: HttpLoggingInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
        converterFactory: Converter.Factory
    ): CarServiceApi {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(userAgentInterceptor)
        }.build()
        val retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.CAR_SERVICE_URL)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(CarServiceApi::class.java)
    }

    @Provides
    fun provideAuthApi(
        userProviderValuesInterceptor: UserProviderValuesInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        errorInterceptor: ErrorInterceptor,
        timeoutInterceptor: TimeoutInterceptor,
        converterFactory: Converter.Factory
    ): AuthApi {
        val retrofit = Retrofit
            .Builder()
            .client(
                OkHttpClient.Builder().apply {
                    addInterceptor(timeoutInterceptor)
                    addInterceptor(userProviderValuesInterceptor)
                    addInterceptor(loggingInterceptor)
                    addInterceptor(errorInterceptor)
                }.build()
            )
            .baseUrl(BuildConfig.OPENSOURCE_URL)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(AuthApi::class.java)
    }

    @Singleton
    @Provides
    fun provideOpenSourceApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): OpenSourceApi {
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.OPENSOURCE_URL)
            .addConverterFactory(converterFactory)
            .build()
        return retrofit.create(OpenSourceApi::class.java)
    }
}


@Module
@InstallIn(SingletonComponent::class)
interface NetworkModuleBinds {
    @Singleton
    @Binds
    fun bindApiEventsProvider(
        events: ApiEventsImpl
    ): ApiEventsProvider

    @Singleton
    @Binds
    fun bindApiEventsControl(
        events: ApiEventsImpl
    ): ApiEventsControl
}