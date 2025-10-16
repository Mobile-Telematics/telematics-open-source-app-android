package com.telematics.features.dashboard.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.NetworkException
import com.telematics.core.data.repository.UserAuthRepository
import com.telematics.core.data.repository.UserProfileRepository
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
class ReAuthViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val userAuthRepository: UserAuthRepository,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase,
) : ViewModel() {

    fun getUserProfileFlow() = userProfileRepository.getUserProfileFlow()

    private var requestDelay = 0L

    private val _uiState: MutableStateFlow<ReAuthUiState> = MutableStateFlow(ReAuthUiState())
    val uiState: StateFlow<ReAuthUiState>
        get() = _uiState.asStateFlow()

    fun reAuthByEmail(email: String, password: String) {
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

            userAuthRepository.signInUserByEmail(email, password).run {
                onSuccess {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            error = null,
                            errorPasswordEnabled = false
                        )
                    }
                }
                onFailure { throwable ->
                    requestDelay += 1000

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable,
                            errorPasswordEnabled = throwable is NetworkException.BadRequestException
                        )
                    }
                }
            }
        }
    }

    fun resetPassword(email: String) {
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

            userAuthRepository.resetPassword(email).run {
                onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLinkSent = true,
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
                isLoggedIn = false
            )
        }
    }

    fun onLinkSentHandled() {
        _uiState.update {
            it.copy(
                isLinkSent = false
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