package com.telematics.features.reward.rewards


import com.telematics.core.model.UserProfile

data class RewardsUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
)