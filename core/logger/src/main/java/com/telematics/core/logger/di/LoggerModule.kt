package com.telematics.core.logger.di

import com.telematics.core.logger.Logger
import com.telematics.core.logger.LoggerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LoggerModuleBinds {
    @Singleton
    @Binds
    fun bindLogger(
        logger: LoggerImpl
    ): Logger
}