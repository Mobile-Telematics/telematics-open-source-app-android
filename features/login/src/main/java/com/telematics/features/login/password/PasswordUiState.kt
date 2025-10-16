package com.telematics.features.login.password

data class PasswordUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isLoggedIn: Boolean = false,
    val isLinkSent: Boolean = false
)