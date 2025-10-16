package com.telematics.core.data.datasource.remote

import android.content.Context
import com.telematics.core.model.company_id.InstanceName
import com.telematics.core.network.api.UserServiceApi
import com.telematics.core.network.mappers.toInstanceName
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class UserServiceRemoteDataSourceImpl @Inject constructor(
    private val userServiceApi: UserServiceApi,
    @param:ApplicationContext val context: Context,
) : UserServiceRemoteDataSource {

    override suspend fun changeCompanyId(companyId: String): InstanceName {

        val response = userServiceApi.sendCompanyId(companyId)
        return if (response.status == 200) {
            response.result.toInstanceName()
        } else InstanceName(null, false)
    }
}

interface UserServiceRemoteDataSource {
    suspend fun changeCompanyId(companyId: String): InstanceName
}