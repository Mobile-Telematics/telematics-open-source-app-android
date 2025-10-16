package com.telematics.features.settings.logbook


data class LogbookUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val logbookRequested: Boolean = false
)