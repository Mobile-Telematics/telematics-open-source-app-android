package com.telematics.core.data.formatter.di

import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.data.formatter.MeasuresFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FormatterModule {
    @Binds
    @Singleton
    fun bindDateFormatter(formatter: MeasuresFormatterImpl): MeasuresFormatter
}