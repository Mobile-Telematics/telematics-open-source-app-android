package com.telematics.core.data.repository

import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.datasource.local.UserAuthLocalDataSource
import com.telematics.core.data.datasource.local.UserProfileLocalDataSource
import com.telematics.core.data.datasource.remote.UserProfileRemoteDataSource
import com.telematics.core.model.UserProfile
import com.telematics.core.network.model.response.asUserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userAuthLocalDataSource: UserAuthLocalDataSource,
    private val intercomRepository: IntercomRepository,
    private val userDataRepository: UserDataRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserProfileRepository {

    override fun getUserProfileFlow(): Flow<UserProfile?> =
        userProfileLocalDataSource.getUserProfileFlow()

    override suspend fun clearUserProfile() =
        withContext(ioDispatcher) {
            userProfileLocalDataSource.clearUserProfile()
        }

    override suspend fun refreshUserProfile(): Result<UserProfile> =
        withContext(ioDispatcher) {
            try {
                val userProfile = if (userAuthLocalDataSource.firebaseId.isBlank()) {
                    userProfileRemoteDataSource.getUserProfileLegacy()
                        .getOrThrow().let {
                            userDataRepository.saveSimpleMode(it.system?.simpleMode ?: false)
                            it.personal!!.asUserProfile()
                        }
                } else {
                    userProfileRemoteDataSource.getUserProfile(userAuthLocalDataSource.firebaseId)
                        .getOrThrow().let {
                            userDataRepository.saveSimpleMode(it.system?.simpleMode ?: false)
                            it.personal!!.asUserProfile()
                        }
                }

                if (userProfileLocalDataSource.userProfile != userProfile) {
                    userProfileLocalDataSource.userProfile = userProfile

                    intercomRepository.updateUser(userDataRepository.getIntercomUserModel())
                }
                Result.success(userProfile)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    override suspend fun updateUserProfile(user: UserProfile): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val oldUser = userProfileLocalDataSource.userProfile
                if (oldUser != user) {
                    userProfileRemoteDataSource.updateUserProfile(
                        userAuthLocalDataSource.firebaseId,
                        user
                    ).getOrThrow()

                    userProfileLocalDataSource.userProfile = user

                    intercomRepository.updateUser(userDataRepository.getIntercomUserModel())

                    Result.success(Unit)
                } else {
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun uploadProfilePicture(filePath: String): Result<UserProfile> =
        withContext(ioDispatcher) {
            try {
                if (userAuthLocalDataSource.firebaseId.isBlank()) {
                    userProfileRemoteDataSource.getUserProfileLegacy()
                        .getOrThrow().personal!!.let { personalResponse ->
                            val userProfile = personalResponse.asUserProfile()
                            userProfileLocalDataSource.userProfile = userProfile
                        }
                }

                userProfileRemoteDataSource.uploadUserImage(
                    userAuthLocalDataSource.firebaseId,
                    filePath
                ).getOrThrow().imageUrl!!.let { imageUrl ->
                    val newUser = userProfileLocalDataSource.userProfile.copy(
                        profileImage = imageUrl
                    )
                    userProfileLocalDataSource.userProfile = newUser

                    Result.success(newUser)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}


interface UserProfileRepository {
    fun getUserProfileFlow(): Flow<UserProfile?>
    suspend fun clearUserProfile()

    suspend fun refreshUserProfile(): Result<UserProfile>

    suspend fun updateUserProfile(user: UserProfile): Result<Unit>
    suspend fun uploadProfilePicture(filePath: String): Result<UserProfile>
}