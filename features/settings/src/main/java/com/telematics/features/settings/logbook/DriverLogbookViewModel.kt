package com.telematics.features.settings.logbook

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.NetworkException
import com.telematics.core.common.extension.isValidEmail
import com.telematics.core.data.repository.LogbookRepository
import com.telematics.core.data.repository.SettingsRepository
import com.telematics.core.data.repository.UserProfileRepository
import com.telematics.core.domain.usecase.HasNetworkConnectionUseCase
import com.telematics.core.model.UserProfile
import com.telematics.core.model.logbook.LogbookDateFormat
import com.telematics.core.model.logbook.LogbookTypeOfReport
import com.telematics.core.model.logbook.LogbookUnits
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.features.settings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DriverLogbookViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val settingsRepository: SettingsRepository,
    private val logbookRepository: LogbookRepository,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    private val _uiState: MutableStateFlow<LogbookUiState> = MutableStateFlow(LogbookUiState())
    val uiState: StateFlow<LogbookUiState>
        get() = _uiState.asStateFlow()

    fun getUserProfileFlow() = userProfileRepository.getUserProfileFlow()

    fun getDistanceMeasure(): DistanceMeasure {
        return settingsRepository.getDistanceMeasure()
    }

    fun sendRequest(
        email: String,
        startDate: String,
        endDate: String,
        units: String,
        dateFormat: String,
        reportType: String
    ) {
        viewModelScope.launch {
            if (email.isEmpty()) {
                _error.emit(context.getString(R.string.logbook_empty_email))
                return@launch
            }

            if (!email.isValidEmail()) {
                _error.emit(context.getString(R.string.logbook_error_email))
                return@launch
            }

            if (startDate.isBlank()) {
                _error.emit(context.getString(R.string.logbook_empty_start_date))
                return@launch
            }

            if (endDate.isBlank()) {
                _error.emit(context.getString(R.string.logbook_empty_end_date))
                return@launch
            }

            val serverDataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            try {
                val start = serverDataFormat.parse(startDate)
                val end = serverDataFormat.parse(endDate)
                if (start!! > end!!) {
                    _error.emit(context.getString(R.string.logbook_error_date))
                    return@launch
                }
            } catch (_: Exception) {
                _error.emit(context.getString(R.string.logbook_error_date))
                return@launch
            }

            if (!hasNetworkConnectionUseCase()) {
                _uiState.update {
                    it.copy(
                        error = NetworkException.NoNetwork
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = true)
            }

            logbookRepository.requestLogbook(
                email = email,
                startDate = startDate,
                endDate = endDate,
                units = units,
                dateFormat = dateFormat,
                reportType = reportType,
            ).run {
                onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            logbookRequested = true,
                            error = null
                        )
                    }
                }
                onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable
                        )
                    }
                }
            }
        }
    }

    fun onErrorHandled() {
        _uiState.update {
            it.copy(
                error = null
            )
        }
    }

}