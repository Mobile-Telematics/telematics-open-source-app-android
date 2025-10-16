package com.telematics.features.settings.company_id

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.extension.setLiveDataForResult
import com.telematics.core.data.repository.UserServiceRepository
import com.telematics.core.model.company_id.InstanceName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class CompanyIdViewModel @Inject constructor(
    private val userServiceRepository: UserServiceRepository
) : ViewModel() {

    fun send(companyId: String): LiveData<Result<InstanceName>> {

        val logoutState = MutableLiveData<Result<InstanceName>>()
        userServiceRepository.changeCompanyId(companyId)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(logoutState)
            .launchIn(viewModelScope)
        return logoutState
    }
}