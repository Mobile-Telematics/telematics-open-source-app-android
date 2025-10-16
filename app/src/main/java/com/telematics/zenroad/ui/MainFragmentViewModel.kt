package com.telematics.zenroad.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.extension.setLiveDataForResult
import com.telematics.core.data.repository.IntercomRepository
import com.telematics.core.data.repository.SettingsRepository
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.data.repository.UserAuthRepository
import com.telematics.core.data.repository.UserDataRepository
import com.telematics.core.data.repository.UserProfileRepository
import com.telematics.core.domain.usecase.HasNetworkConnectionUseCase
import com.telematics.core.domain.usecase.NotificationPermissionUseCase
import com.telematics.core.model.TripRecordMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val userDataRepository: UserDataRepository,
    private val userAuthRepository: UserAuthRepository,
    private val notificationPermissionUseCase: NotificationPermissionUseCase,
    private val settingsRepository: SettingsRepository,
    private val intercomRepository: IntercomRepository,
    private val userProfileRepository: UserProfileRepository,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase,
) : ViewModel() {

    init {
        refreshUserProfile()
    }

    fun getUserProfileFlow() = userProfileRepository.getUserProfileFlow()

    fun getSimpleModeFlow() = userDataRepository.getSimpleModeFlow()

    private fun refreshUserProfile() {
        viewModelScope.launch {

            if (hasNetworkConnectionUseCase()) {
                userProfileRepository.refreshUserProfile()
            }
        }
    }

    fun updateIntercomUser() {
        viewModelScope.launch {
            intercomRepository.updateUser(userDataRepository.getIntercomUserModel())
        }
    }

    fun setIntentForNotification(intent: Intent) {
        trackingRepository.setIntentForNotification(intent)
    }

    fun setDeviceTokenForTrackingApi() {
        flow {
            val deviceToken = userAuthRepository.getDeviceToken()
            val value = trackingRepository.setDeviceToken(deviceToken)
            emit(value)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun checkPermissions(): LiveData<Result<Boolean>> {
        val permissionsState = MutableLiveData<Result<Boolean>>()
        trackingRepository.checkPermissions()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(permissionsState)
            .launchIn(viewModelScope)
        return permissionsState
    }

    fun checkNotificationPermissions(): LiveData<Boolean> {
        val permissionsState = MutableLiveData<Boolean>()
        notificationPermissionUseCase()
            .flowOn(Dispatchers.IO)
            .onEach {
                permissionsState.postValue(it)
            }
            .launchIn(viewModelScope)
        return permissionsState
    }

    fun onNotificationPermissionRequested() {
        flow {
            settingsRepository.setNotificationPermissionCompleted()
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun startWizard(activity: Activity) {
        trackingRepository.checkPermissionAndStartWizard(activity)
    }

    fun enableTracking() {
        viewModelScope.launch {
            trackingRepository.getTripRecordMode().run {
                onSuccess {
                    when {
                        it.first == TripRecordMode.DISABLED -> {}
                        it.first == TripRecordMode.ALWAYS_ON -> trackingRepository.enableTrackingSDK()
                        it.second -> trackingRepository.enableTrackingSDK()
                    }
                }
            }
        }
    }

    private val saveStateBundle = MutableLiveData<Bundle>()
    val getSaveStateBundle: LiveData<Bundle>
        get() {
            return saveStateBundle
        }

    fun saveCurrentBottomMenuState(state: Int) {

        val bundle = bundleOf("bottom_state" to state)
        saveStateBundle.value = bundle
    }

    fun bundleToListSize(bundle: Bundle): Int {

        return bundle.getInt("bottom_state", 0)
    }

    fun showChat() {
        intercomRepository.showIntercomHomeSpace()
    }
}