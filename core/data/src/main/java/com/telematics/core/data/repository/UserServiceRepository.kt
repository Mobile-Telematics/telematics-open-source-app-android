package com.telematics.core.data.repository

import com.telematics.core.data.datasource.remote.UserServiceRemoteDataSource
import com.telematics.core.model.company_id.InstanceName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserServiceRepositoryImpl @Inject constructor(
    private val userServiceRemoteDataSource: UserServiceRemoteDataSource,
) : UserServiceRepository {
    override fun changeCompanyId(companyId: String): Flow<InstanceName> {

        return flow {
            val data = userServiceRemoteDataSource.changeCompanyId(companyId)
            emit(data)
        }
    }
}

interface UserServiceRepository {
    fun changeCompanyId(companyId: String): Flow<InstanceName>
}