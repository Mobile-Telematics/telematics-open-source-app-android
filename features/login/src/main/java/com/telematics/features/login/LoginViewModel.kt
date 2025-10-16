package com.telematics.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.NetworkException
import com.telematics.core.data.repository.UserAuthRepository
import com.telematics.core.domain.usecase.HasNetworkConnectionUseCase
import com.telematics.core.model.UserTypeByEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private var userAuthRepository: UserAuthRepository,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase,
) : ViewModel() {
    private var requestDelay = 0L

    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState>
        get() = _uiState.asStateFlow()

    fun checkUserByEmail(email: String) {
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

            userAuthRepository.checkUserByEmail(email).run {
                onSuccess { userState ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userState = userState,
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
                userState = UserTypeByEmail.UNDEFINED
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