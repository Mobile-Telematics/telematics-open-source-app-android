package com.telematics.features.feed.ui.feed

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.extension.setLiveDataForResult
import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.data.repository.SettingsRepository
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.model.tracking.TripData
import com.telematics.core.model.tracking.TripDetailsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val measuresFormatter: MeasuresFormatter,
    private val settingsRepository: SettingsRepository,
    val formatter: MeasuresFormatter,
) : ViewModel() {

    companion object {
        private const val SAVE_LIST_STATE_BUNDLE_KEY = "SAVE_LIST_STATE_BUNDLE_KEY"

        private const val LOADING_COUNT = 20
    }

    val getMeasuresFormatter: MeasuresFormatter
        get() {
            return measuresFormatter
        }

    private val saveStateBundle = MutableLiveData<Bundle>()
    val getSaveStateBundle: LiveData<Bundle>
        get() {
            return saveStateBundle
        }

    fun getTripList(offset: Int, count: Int = LOADING_COUNT): LiveData<Result<List<TripData>>> {

        val tripDataState = MutableLiveData<Result<List<TripData>>>()
        trackingRepository.getTrips(offset, count)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(tripDataState)
            .launchIn(viewModelScope)
        return tripDataState
    }

    fun getPermissionLink(context: Context): String {

        return settingsRepository.getTelematicsLink(context)
    }

    fun changeTripTypeTo(tripId: String, tripType: TripData.TripType): LiveData<Result<Boolean>> {

        val changeState = MutableLiveData<Result<Boolean>>()
        trackingRepository.changeTripType(tripId, tripType)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(changeState)
            .launchIn(viewModelScope)
        return changeState
    }

    fun setDeleteStatus(tripData: TripData): LiveData<Result<Unit>> {

        val deleteState = MutableLiveData<Result<Unit>>()
        trackingRepository.setDeleteStatus(tripData.id!!)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(deleteState)
            .launchIn(viewModelScope)
        return deleteState
    }

    fun changeTripTag(tripData: TripData): LiveData<Result<Unit>> {

        val changeTripTagState = MutableLiveData<Result<Unit>>()
        trackingRepository.changeTripTag(tripData.id!!, tripData.tag.type)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(changeTripTagState)
            .launchIn(viewModelScope)
        return changeTripTagState
    }

    fun hideTrip(tripData: TripData): LiveData<Result<Unit>> {

        val deleteState = MutableLiveData<Result<Unit>>()
        trackingRepository.hideTrip(tripData.id!!)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(deleteState)
            .launchIn(viewModelScope)
        return deleteState
    }

    fun saveCurrentListSize(tripListSize: Int) {

        val bundle = bundleOf(SAVE_LIST_STATE_BUNDLE_KEY to tripListSize)
        saveStateBundle.value = bundle
    }

    fun bundleToListSize(bundle: Bundle): Int {

        return bundle.getInt(SAVE_LIST_STATE_BUNDLE_KEY, 0)
    }

    var tripDetails: TripDetailsData? = null
        private set

    fun getTripDetailsByPos(listItemPosition: Int): LiveData<Result<TripDetailsData?>> {

        val tripDataState = MutableLiveData<Result<TripDetailsData?>>()
        trackingRepository.getTripDetailsByPos(listItemPosition)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(tripDataState)
            .map { tripDetailsData ->
                tripDetails = tripDetailsData
            }
            .launchIn(viewModelScope)
        return tripDataState
    }
}