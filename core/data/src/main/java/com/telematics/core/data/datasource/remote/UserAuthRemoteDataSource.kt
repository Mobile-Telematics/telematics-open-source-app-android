package com.telematics.core.data.datasource.remote

import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.network.api.AuthApi
import com.telematics.core.network.util.createEmptyResult
import com.telematics.core.network.util.createResult
import com.telematics.core.network.model.request.CheckUserByEmailRequest
import com.telematics.core.network.model.request.RegisterUserByEmailRequest
import com.telematics.core.network.model.request.ResetPasswordRequest
import com.telematics.core.network.model.request.SignInRequest
import com.telematics.core.network.model.response.CheckUserByEmailResponse
import com.telematics.core.network.model.response.SignInResponse
import com.telematics.core.network.model.response.SignUpResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserAuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserAuthRemoteDataSource {
    override suspend fun checkUserByEmail(email: String): Result<CheckUserByEmailResponse> =
        withContext(ioDispatcher) {
            try {
                api.checkUserByEmail(
                    CheckUserByEmailRequest(
                        email = email
                    )
                ).createResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun registerUserByEmail(
        email: String,
        password: String
    ): Result<SignUpResponse> =
        withContext(ioDispatcher) {
            try {
                api.registerUserByEmail(
                    RegisterUserByEmailRequest(
                        RegisterUserByEmailRequest.UserProfileRequest(
                            email = email,
                            password = password
                        )
                    )
                ).createResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun resetPassword(email: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                api.resetPassword(
                    ResetPasswordRequest(
                        email = email
                    )
                ).createEmptyResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun signInUserByEmail(
        email: String,
        password: String
    ): Result<SignInResponse> =
        withContext(ioDispatcher) {
            try {
                api.signInUserByEmail(
                    SignInRequest(
                        email = email,
                        password = password
                    )
                ).createResult()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface UserAuthRemoteDataSource {
    suspend fun checkUserByEmail(email: String): Result<CheckUserByEmailResponse>
    suspend fun registerUserByEmail(email: String, password: String): Result<SignUpResponse>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signInUserByEmail(email: String, password: String): Result<SignInResponse>
}