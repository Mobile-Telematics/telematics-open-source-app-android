package com.telematics.core.network.api

import com.telematics.core.network.model.refresh_token.RefreshRequest
import com.telematics.core.network.model.rest.ApiResponse
import com.telematics.core.network.model.rest.ApiResult
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {

    // refresh token
    @POST("v1/Auth/RefreshToken")
    suspend fun refreshToken(@Body refreshRequest: RefreshRequest): ApiResponse<ApiResult>
}