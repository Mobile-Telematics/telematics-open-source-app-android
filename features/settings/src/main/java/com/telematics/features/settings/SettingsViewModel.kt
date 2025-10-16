package com.telematics.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.data.repository.IntercomRepository
import com.telematics.core.data.repository.SessionRepository
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.data.repository.UserDataRepository
import com.telematics.core.data.repository.UserProfileRepository
import com.telematics.core.domain.usecase.LogoutUseCase
import com.telematics.core.model.TripRecordMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _logout: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val logout: SharedFlow<Boolean>
        get() = _logout.asSharedFlow()


    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _logout.emit(true)
        }
    }
}