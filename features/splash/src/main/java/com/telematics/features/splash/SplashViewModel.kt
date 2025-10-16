package com.telematics.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.data.repository.IntercomRepository
import com.telematics.core.data.repository.OnboardingRepository
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
class SplashViewModel @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val onboardingRepository: OnboardingRepository,
    private val userProfileRepository: UserProfileRepository,
    private val intercomRepository: IntercomRepository,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SplashUiState> =
        MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState>
        get() = _uiState.asStateFlow()

    fun checkState() {
        viewModelScope.launch {
            userAuthRepository.isSessionAvailable().run {
                onSuccess { sessionState ->
                    if (sessionState) {
                        refreshUserProfile()
                    } else {
                        delay(2000)
                        onboardingRepository.needOnboarding().run {
                            onSuccess { onboardingState ->
                                val navigationState = if (onboardingState) {
                                    NavigationState.OnboardingScreen
                                } else {
                                    NavigationState.LoginScreen
                                }
                                _uiState.update { state ->
                                    state.copy(
                                        navigationState = navigationState
                                    )
                                }
                            }
                            onFailure {
                                _uiState.update { state ->
                                    state.copy(
                                        navigationState = NavigationState.OnboardingScreen
                                    )
                                }
                            }
                        }
                    }
                }
                onFailure {
                    _uiState.update { state ->
                        state.copy(
                            navigationState = NavigationState.OnboardingScreen
                        )
                    }
                }
            }
        }
    }

    private fun refreshUserProfile() {
        viewModelScope.launch {

            if (!hasNetworkConnectionUseCase()) {
                _uiState.update {
                    it.copy(
                        navigationState = NavigationState.MainScreen
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = true)
            }

            userProfileRepository.refreshUserProfile()
            intercomRepository.updateIntercomUser()

            _uiState.update {
                it.copy(
                    navigationState = NavigationState.MainScreen
                )
            }
        }
    }

    fun onNavigationStateHandled() {
        _uiState.update {
            it.copy(
                navigationState = NavigationState.UnknownState
            )
        }
    }

    sealed class NavigationState {
        data object MainScreen : NavigationState()
        data object OnboardingScreen : NavigationState()
        data object LoginScreen : NavigationState()
        data object UnknownState : NavigationState()
    }
}