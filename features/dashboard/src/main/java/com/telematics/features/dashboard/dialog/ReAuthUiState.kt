package com.telematics.features.dashboard.dialog

data class ReAuthUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val errorPasswordEnabled: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isLinkSent: Boolean = false
)