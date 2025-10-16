package com.telematics.features.account.ui.account.vehicle


import com.telematics.core.model.UserProfile

data class VehicleUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
)