package com.telematics.core.data.datasource.remote

import android.content.Context
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.network.api.OpenSourceApi
import com.telematics.core.network.util.createResult
import com.telematics.core.network.model.request.LogbookRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class LogbookRemoteDataSourceImpl @Inject constructor(
    private val exportsApi: OpenSourceApi,
    @param:ApplicationContext val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LogbookRemoteDataSource {

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
                exportsApi.requestLogbook(
                    model = LogbookRequest(
                        toEmail = email,
                        startDate = startDate,
                        endDate = endDate,
                        units = units,
                        dateFormat = dateFormat,
                        typeOfReport = reportType,
                        reportFormat = reportFormat
                    )
                ).createResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface LogbookRemoteDataSource {
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