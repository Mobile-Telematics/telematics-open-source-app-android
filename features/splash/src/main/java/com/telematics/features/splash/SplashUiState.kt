package com.telematics.features.splash

data class SplashUiState(
    val isLoading: Boolean = false,
    val navigationState: SplashViewModel.NavigationState = SplashViewModel.NavigationState.UnknownState,
)