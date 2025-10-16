package com.telematics.features.account.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.NetworkException
import com.telematics.core.data.repository.UserProfileRepository
import com.telematics.core.domain.usecase.HasNetworkConnectionUseCase
import com.telematics.core.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState>
        get() = _uiState.asStateFlow()

    fun getUserProfileFlow() = userProfileRepository.getUserProfileFlow()

    fun updateUserProfile(user: UserProfile) {
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

            userProfileRepository.updateUserProfile(user).run {
                onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profileSaved = true,
                            error = null
                        )
                    }
                }
                onFailure { throwable ->
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

    fun onErrorHandled() {
        _uiState.update {
            it.copy(
                error = null
            )
        }
    }
}