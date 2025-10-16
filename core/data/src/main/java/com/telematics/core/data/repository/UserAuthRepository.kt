package com.telematics.core.data.repository

import com.telematics.core.common.NetworkException
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.datasource.local.UserAuthLocalDataSource
import com.telematics.core.data.datasource.local.UserDataLocalDataSource
import com.telematics.core.data.datasource.local.UserDataLocalDataSourceImpl
import com.telematics.core.data.datasource.local.UserProfileLocalDataSource
import com.telematics.core.data.datasource.remote.UserAuthRemoteDataSource
import com.telematics.core.model.SessionData
import com.telematics.core.model.UserTypeByEmail
import com.telematics.core.model.getEmptyUserProfile
import com.telematics.core.network.mappers.toUserTypeByEmail
import com.telematics.core.network.model.response.asUserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserAuthRepositoryImpl @Inject constructor(
    private val userAuthRemoteDataSource: UserAuthRemoteDataSource,
    private val userAuthLocalDataSource: UserAuthLocalDataSource,
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userDataLocalDataSource: UserDataLocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserAuthRepository {
    override suspend fun checkUserByEmail(email: String): Result<UserTypeByEmail> =
        withContext(ioDispatcher) {
            try {
                Result.success(
                    userAuthRemoteDataSource
                        .checkUserByEmail(email)
                        .getOrThrow()
                        .toUserTypeByEmail()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun registerUserByEmail(
        email: String,
        password: String
    ): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                userAuthRemoteDataSource
                    .registerUserByEmail(email, password)
                    .getOrThrow().let { response ->
                        with(response) {
                            personal.also { personalResponse ->
                                userProfileLocalDataSource.userProfile =
                                    personalResponse?.asUserProfile() ?: getEmptyUserProfile()
                            }

                            userAuthLocalDataSource.accessToken = system?.accessToken ?: ""
                            userAuthLocalDataSource.refreshToken = system?.refreshToken ?: ""
                            userAuthLocalDataSource.deviceToken = system?.telematicsUserId ?: ""
                            userAuthLocalDataSource.firebaseId = system?.firebaseId ?: ""
                            userAuthLocalDataSource.userId = system?.telematicsUserId

                            userDataLocalDataSource.simpleMode = system?.simpleMode ?: false

                        }
                        if (userAuthLocalDataSource.session.isEmpty() || userAuthLocalDataSource.deviceToken.isBlank()) {
                            Result.failure(
                                NetworkException.UnknownException()
                            )
                        } else {
                            Result.success(Unit)
                        }
                    }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    override suspend fun signInUserByEmail(email: String, password: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                userAuthRemoteDataSource.signInUserByEmail(email, password)
                    .getOrThrow().let { response ->
                        with(response) {
                            personal.also { personalResponse ->
                                userProfileLocalDataSource.userProfile =
                                    personalResponse?.asUserProfile() ?: getEmptyUserProfile()
                            }

                            userAuthLocalDataSource.accessToken = system?.accessToken ?: ""
                            userAuthLocalDataSource.refreshToken = system?.refreshToken ?: ""
                            userAuthLocalDataSource.deviceToken = system?.telematicsUserId ?: ""
                            userAuthLocalDataSource.firebaseId = system?.firebaseId ?: ""
                            userAuthLocalDataSource.userId = system?.telematicsUserId

                            userDataLocalDataSource.simpleMode = system?.simpleMode ?: false
                        }
                        if (userAuthLocalDataSource.session.isEmpty() || userAuthLocalDataSource.deviceToken.isBlank()) {
                            Result.failure(
                                NetworkException.UnknownException()
                            )
                        } else {
                            Result.success(Unit)
                        }
                    }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun resetPassword(email: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                Result.success(
                    userAuthRemoteDataSource
                        .resetPassword(email = email)
                        .getOrThrow()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun saveSession(session: SessionData) {
        userAuthLocalDataSource.session = session
    }

    override fun getSession(): SessionData =
        userAuthLocalDataSource.session

    override suspend fun isSessionAvailable(): Result<Boolean> =
        withContext(ioDispatcher) {
            try {
                Result.success(
                    !(userAuthLocalDataSource.session.isEmpty() || userAuthLocalDataSource.deviceToken.isBlank())
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun getDeviceToken(): String =
        userAuthLocalDataSource.deviceToken

    override fun saveStateForRewardInviteScreen() {
        userAuthLocalDataSource.rewardInviteState = true
    }

    override fun isRewardInviteScreenOpened(): Boolean =
        userAuthLocalDataSource.rewardInviteState

    override fun clearStateForRewardInviteScreen() {
        userAuthLocalDataSource.rewardInviteState = false
    }
}

interface UserAuthRepository {
    suspend fun checkUserByEmail(email: String): Result<UserTypeByEmail>
    suspend fun registerUserByEmail(email: String, password: String): Result<Unit>

    suspend fun signInUserByEmail(email: String, password: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>

    fun saveSession(session: SessionData)
    fun getSession(): SessionData
    suspend fun isSessionAvailable(): Result<Boolean>

    fun getDeviceToken(): String

    fun saveStateForRewardInviteScreen()
    fun isRewardInviteScreenOpened(): Boolean
    fun clearStateForRewardInviteScreen()
}