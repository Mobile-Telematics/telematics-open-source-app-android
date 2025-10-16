package com.telematics.core.data.repository

import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.datasource.remote.VideoRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videoRemoteDataSource: VideoRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : VideoRepository {

    override suspend fun getVideoUrl(videoID: String): Result<String> =
        withContext(ioDispatcher) {
            try {
                videoRemoteDataSource
                    .getVideoUrl(videoID)
                    .getOrThrow().let { response ->
                        Result.success(
                            response.videoURL!!
                        )
                    }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}


interface VideoRepository {
    suspend fun getVideoUrl(videoID: String): Result<String>
}