package com.telematics.core.network.api

import com.telematics.core.network.model.request.CheckUserByEmailRequest
import com.telematics.core.network.model.request.RegisterUserByEmailRequest
import com.telematics.core.network.model.request.ResetPasswordRequest
import com.telematics.core.network.model.request.SignInRequest
import com.telematics.core.network.model.response.CheckUserByEmailResponse
import com.telematics.core.network.model.response.SignInResponse
import com.telematics.core.network.model.response.SignUpResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("mobileapp/v1/check_and_set_password")
    suspend fun checkUserByEmail(
        @Body body: CheckUserByEmailRequest
    ): Response<CheckUserByEmailResponse>

    @POST("mobileapp/v1/signup")
    suspend fun registerUserByEmail(
        @Body body: RegisterUserByEmailRequest
    ): Response<SignUpResponse>

    @POST("mobileapp/v1/reset_password")
    suspend fun resetPassword(
        @Body body: ResetPasswordRequest
    ): Response<ResponseBody>

    @POST("mobileapp/v1/signin")
    suspend fun signInUserByEmail(
        @Body body: SignInRequest
    ): Response<SignInResponse>
}