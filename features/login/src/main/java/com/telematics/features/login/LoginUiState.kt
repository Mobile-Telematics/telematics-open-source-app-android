package com.telematics.features.login

import com.telematics.core.model.UserTypeByEmail

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val userState: UserTypeByEmail = UserTypeByEmail.UNDEFINED
)