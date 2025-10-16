package com.telematics.zenroad.di

import com.telematics.core.common.di.VersionHeader
import com.telematics.zenroad.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RequestContextModule {
    @VersionHeader
    @Provides
    fun provideVersion(): String = BuildConfig.VERSION_NAME

}
