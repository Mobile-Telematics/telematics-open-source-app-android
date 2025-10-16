package com.telematics.features.login.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.NetworkException
import com.telematics.core.data.repository.IntercomRepository
import com.telematics.core.data.repository.UserAuthRepository
import com.telematics.core.domain.usecase.HasNetworkConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val intercomRepository: IntercomRepository,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase,
) : ViewModel() {

    private var requestDelay = 0L

    private val _uiState: MutableStateFlow<RegistrationUiState> =
        MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState>
        get() = _uiState.asStateFlow()

    fun registerUserByEmail(email: String, password: String) {
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

            userAuthRepository.registerUserByEmail(email, password).run {
                onSuccess {
                    intercomRepository.createIntercomCurrentUser()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true,
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

    fun onUserStateHandled() {
        _uiState.update {
            it.copy(
                isRegistered = false
            )
        }
    }

    fun onErrorHandled() {
        _uiState.update {
            it.copy(
                error = null
            )
        }
    }
}