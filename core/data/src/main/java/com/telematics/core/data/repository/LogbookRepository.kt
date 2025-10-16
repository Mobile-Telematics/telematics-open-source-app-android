package com.telematics.core.data.repository

import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.datasource.remote.LogbookRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogbookRepositoryImpl @Inject constructor(
    private val logbookRemoteDataSource: LogbookRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LogbookRepository {

    override suspend fun requestLogbook(
        email: String,
        startDate: String,
        endDate: String,
        units: String,
        dateFormat: String,
        reportType: String,
        reportFormat: String
    ): Result<Any> =
        withContext(ioDispatcher) {
            try {
                logbookRemoteDataSource
                    .requestLogbook(
                        email = email,
                        startDate = startDate,
                        endDate = endDate,
                        units = units,
                        dateFormat = dateFormat,
                        reportType = reportType,
                        reportFormat = reportFormat
                    )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface LogbookRepository {
    suspend fun requestLogbook(
        email: String,
        startDate: String,
        endDate: String,
        units: String,
        dateFormat: String,
        reportType: String,
        reportFormat: String = "csv"
    ): Result<Any>
}