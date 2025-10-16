package com.telematics.features.feed.ui.trip_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.extension.setLiveDataForResult
import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.data.repository.SettingsRepository
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.model.measures.MapStyle
import com.telematics.core.model.tracking.ChangeTripEvent
import com.telematics.core.model.tracking.TripData
import com.telematics.core.model.tracking.TripDetailsData
import com.telematics.core.model.tracking.TripPointData
import com.telematics.features.feed.model.AlertType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val settingsRepository: SettingsRepository,
    val formatter: MeasuresFormatter
) : ViewModel() {

    private var currentTripId: String? = null

    fun getTripDetailsByPos(position: Int): LiveData<Result<TripDetailsData?>> {

        val tripDataState = MutableLiveData<Result<TripDetailsData?>>()
        trackingRepository.getTripDetailsByPos(position)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(tripDataState)
            .map { tripDetailsData ->
                currentTripId = tripDetailsData?.id
            }
            .launchIn(viewModelScope)
        return tripDataState
    }

    fun changeTripTypeTo(tripId: String, tripType: TripData.TripType): LiveData<Result<Boolean>> {

        val changeState = MutableLiveData<Result<Boolean>>()
        trackingRepository.changeTripType(tripId, tripType)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(changeState)
            .launchIn(viewModelScope)
        return changeState
    }

    fun changeTripEvent(
        event: TripPointData,
        alertType: AlertType
    ): LiveData<Result<Boolean>> {

        val tripId = currentTripId.orEmpty()

        val changeTripEvent = ChangeTripEvent(
            AlertType.from(event.alertType).toString(),
            event.latitude,
            event.longitude,
            event.fullDate,
            alertType.toString()
        )
        val changeState = MutableLiveData<Result<Boolean>>()
        trackingRepository.changeTripEvent(tripId, changeTripEvent)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(changeState)
            .launchIn(viewModelScope)
        return changeState
    }

    fun changeTripTagTo(tripId: String, tagType: TripData.TagType): LiveData<Result<Unit>> {

        val changeTripTagState = MutableLiveData<Result<Unit>>()
        trackingRepository.changeTripTag(tripId, tagType)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(changeTripTagState)
            .launchIn(viewModelScope)
        return changeTripTagState
    }

    fun hideTrip(tripId: String): LiveData<Result<Unit>> {

        val deleteState = MutableLiveData<Result<Unit>>()
        trackingRepository.hideTrip(tripId)
            .flowOn(Dispatchers.Main)
            .setLiveDataForResult(deleteState)
            .launchIn(viewModelScope)
        return deleteState
    }

    fun setDeleteStatus(tripId: String): LiveData<Result<Unit>> {

        val deleteState = MutableLiveData<Result<Unit>>()
        trackingRepository.setDeleteStatus(tripId)
            .flowOn(Dispatchers.Main)
            .setLiveDataForResult(deleteState)
            .launchIn(viewModelScope)
        return deleteState
    }

    fun getMapStyle(): MapStyle {
        return settingsRepository.getMapStyle()
    }
}