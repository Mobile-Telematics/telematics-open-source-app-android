package com.telematics.features.account.ui.account


data class AccountUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)