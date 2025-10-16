package com.telematics.core.network.api

import com.telematics.core.network.model.company_id.InstanceNameBody
import com.telematics.core.network.model.rest.ApiResponse
import retrofit2.http.POST
import retrofit2.http.Path

interface UserServiceApi {

    @POST("/v1/Management/users/instances/change/{companyId}")
    suspend fun sendCompanyId(@Path("companyId") companyId: String): ApiResponse<InstanceNameBody>
}