package com.telematics.zenroad.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.model.TripRecordMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository
) : ViewModel() {

    fun allPermissionsGranted() {
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
}