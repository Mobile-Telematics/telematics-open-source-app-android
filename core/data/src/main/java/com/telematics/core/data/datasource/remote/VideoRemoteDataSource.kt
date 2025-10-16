package com.telematics.core.data.datasource.remote

import android.content.Context
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.network.model.settings.VideoPreviewResponse
import com.telematics.core.network.model.settings.VideoUrlResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class VideoRemoteDataSourceImpl @Inject constructor(
    @param:ApplicationContext val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : VideoRemoteDataSource {

    override suspend fun getVideoPreview(userId: String): Result<VideoPreviewResponse> =
        withContext(ioDispatcher) {
            try {
                // TODO
                throw Exception("Not implemented")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getVideoUrl(videoID: String): Result<VideoUrlResponse> =
        withContext(ioDispatcher) {
            try {
                // TODO
                throw Exception("Not implemented")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface VideoRemoteDataSource {
    suspend fun getVideoPreview(userId: String): Result<VideoPreviewResponse>
    suspend fun getVideoUrl(videoID: String): Result<VideoUrlResponse>
}