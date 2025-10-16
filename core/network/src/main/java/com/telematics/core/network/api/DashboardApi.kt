package com.telematics.core.network.api

import com.telematics.core.network.model.request.DashboardRequest
import com.telematics.core.network.model.response.DashboardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DashboardApi {

    @POST("mobileapp/v1/dashboard")
    suspend fun fetchDashboardData(
        @Body model: DashboardRequest
    ): Response<DashboardResponse>
}