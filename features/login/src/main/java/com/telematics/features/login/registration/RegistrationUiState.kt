package com.telematics.features.login.registration

data class RegistrationUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isRegistered: Boolean = false
)