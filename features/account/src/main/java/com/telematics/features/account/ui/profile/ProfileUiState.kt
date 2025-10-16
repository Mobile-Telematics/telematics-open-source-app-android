package com.telematics.features.account.ui.profile


data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val profileSaved: Boolean = false
)