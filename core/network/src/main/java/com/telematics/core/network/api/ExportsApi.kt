package com.telematics.core.network.api

import com.telematics.core.network.model.request.DashboardRequest
import com.telematics.core.network.model.request.LogbookRequest
import com.telematics.core.network.model.response.DashboardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ExportsApi {

    @POST("mobileapp/v1/exports/trip-logbook")
    suspend fun requestLogbook(
        @Body model: LogbookRequest
    ): Response<DashboardResponse>
}