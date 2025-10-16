package com.telematics.features.account.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.NetworkException
import com.telematics.core.common.extension.setLiveDataForResult
import com.telematics.core.data.repository.UserDataRepository
import com.telematics.core.data.repository.UserProfileRepository
import com.telematics.core.domain.usecase.HasNetworkConnectionUseCase
import com.telematics.core.domain.usecase.VehicleUseCase
import com.telematics.core.model.carservice.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val userDataRepository: UserDataRepository,
    private val vehicleUseCase: VehicleUseCase,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase
) : ViewModel() {

    init {
        refreshUserProfile()
    }

    private var requestDelay = 0L

    private val _uiState: MutableStateFlow<AccountUiState> = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState>
        get() = _uiState.asStateFlow()

    fun getUserProfileFlow() = userProfileRepository.getUserProfileFlow()
    val simpleModeFlow = userDataRepository.getSimpleModeFlow()

    private fun refreshUserProfile() {
        viewModelScope.launch {

            if (hasNetworkConnectionUseCase()) {
                userProfileRepository.refreshUserProfile()
            }
        }
    }

    fun uploadProfilePicture(filePath: String) {
        viewModelScope.launch {

            if (!hasNetworkConnectionUseCase()) {
                _uiState.update {
                    it.copy(
                        error = NetworkException.NoNetwork
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = true)
            }

            delay(requestDelay)

            userProfileRepository.uploadProfilePicture(filePath).run {
                onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                }
                onFailure { throwable ->
                    requestDelay += 1000

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable
                        )
                    }
                }
            }
        }
    }

    fun getVehicles(): LiveData<Result<List<Vehicle>>> {

        val vehiclesState = MutableLiveData<Result<List<Vehicle>>>()
        vehicleUseCase.getVehicles()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(vehiclesState)
            .launchIn(viewModelScope)
        return vehiclesState
    }

    fun onErrorHandled() {
        _uiState.update {
            it.copy(
                error = null
            )
        }
    }
}