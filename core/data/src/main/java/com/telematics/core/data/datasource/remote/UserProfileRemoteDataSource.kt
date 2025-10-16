package com.telematics.core.data.datasource.remote

import android.content.Context
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.model.UserProfile
import com.telematics.core.network.api.OpenSourceApi
import com.telematics.core.network.util.createEmptyResult
import com.telematics.core.network.util.createResult
import com.telematics.core.network.model.request.asUpdateUserProfileRequest
import com.telematics.core.network.model.response.GetUserProfileResponse
import com.telematics.core.network.model.response.UploadUserImageResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.UUID
import javax.inject.Inject


class UserProfileRemoteDataSourceImpl @Inject constructor(
    private val api: OpenSourceApi,
    @param:ApplicationContext val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserProfileRemoteDataSource {
    override suspend fun getUserProfile(userId: String): Result<GetUserProfileResponse> =
        withContext(ioDispatcher) {
            try {
                api.getUserProfile(userId).createResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun updateUserProfile(userId: String, profile: UserProfile): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                api.updateUserProfile(
                    userId,
                    profile.asUpdateUserProfileRequest()
                ).createEmptyResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun uploadUserImage(
        userId: String,
        filePath: String
    ): Result<UploadUserImageResponse> =
        withContext(ioDispatcher) {
            try {

                val file = File(filePath)

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

                // MultipartBody.Part is used to send also the actual file name
                val imageBody = MultipartBody.Part.createFormData(
                    "image",
                    UUID.randomUUID().toString(),
                    requestFile
                )

                // Add user ID
                val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                api.uploadUserImage(
                    userId = userIdBody,
                    image = imageBody
                ).createResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getUserProfileLegacy(): Result<GetUserProfileResponse> =
        withContext(ioDispatcher) {
            try {
                api.getUserProfileLegacy().createResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteUserProfile(userId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                api.deleteUserProfile(userId).createEmptyResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface UserProfileRemoteDataSource {
    suspend fun getUserProfile(userId: String): Result<GetUserProfileResponse>
    suspend fun updateUserProfile(userId: String, profile: UserProfile): Result<Unit>
    suspend fun uploadUserImage(userId: String, filePath: String): Result<UploadUserImageResponse>
    suspend fun getUserProfileLegacy(): Result<GetUserProfileResponse>
    suspend fun deleteUserProfile(userId: String): Result<Unit>
}