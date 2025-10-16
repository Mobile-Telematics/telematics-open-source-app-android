package com.telematics.core.data.datasource.remote

import android.content.Context
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.network.api.DriveCoinsApi
import com.telematics.core.network.model.statistics.DriveCoinsRest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DriveCoinsRemoteDataSourceImpl @Inject constructor(
    private val driveCoinsApi: DriveCoinsApi,
    @param:ApplicationContext val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DriveCoinsRemoteDataSource {
    override suspend fun getDrivingCoins(
        startDate: String,
        endDate: String
    ): Result<DriveCoinsRest> =
        withContext(ioDispatcher) {
            try {
                driveCoinsApi.getDriveCoinsIndividual(
                    startDate,
                    endDate
                ).result!!.let { Result.success(it) }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface DriveCoinsRemoteDataSource {
    suspend fun getDrivingCoins(
        startDate: String,
        endDate: String
    ): Result<DriveCoinsRest>
}