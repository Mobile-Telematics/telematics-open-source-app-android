package ui

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.extension.setLiveDataForResult
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.domain.usecase.VehicleUseCase
import com.telematics.core.model.carservice.Vehicle
import com.telematics.core.model.tracking.ElmDevice
import com.telematics.core.model.tracking.ElmManagerLinkingResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class ObdViewModel @Inject constructor(
    private val vehicleUseCase: VehicleUseCase,
    private val trackingRepository: TrackingRepository
) : ViewModel() {

    fun getVehicles(): LiveData<Result<List<Vehicle>>> {

        val vehiclesState = MutableLiveData<Result<List<Vehicle>>>()
        vehicleUseCase.getVehicles()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(vehiclesState)
            .launchIn(viewModelScope)
        return vehiclesState
    }

    fun getLastSession(): LiveData<Result<Long>> {

        val sessionState = MutableLiveData<Result<Long>>()
        trackingRepository.getLastSession()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(sessionState)
            .launchIn(viewModelScope)
        return sessionState
    }

    @Suppress("UNUSED_PARAMETER")
    fun uploadOdometerPhoto(filePath: String): LiveData<Result<Unit>> {

        val uploadState = MutableLiveData<Result<Unit>>()
        flow {
            delay(2000)
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(uploadState)
            .launchIn(viewModelScope)
        return uploadState
    }

    fun getBluetoothAdapter(context: Context): BluetoothAdapter? =
        trackingRepository.getBluetoothAdapter(context)

    fun getRequestBluetoothEnableCode() = trackingRepository.getRequestBluetoothEnableCode()

    fun registerElmManagerLinkingResult(): LiveData<Result<ElmManagerLinkingResult?>> {

        val elmDeviceState = MutableLiveData<Result<ElmManagerLinkingResult?>>()
        trackingRepository.getElmManagerLinkingResult()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(elmDeviceState)
            .launchIn(viewModelScope)

        return elmDeviceState
    }


    fun getElmDevices() {

        trackingRepository.getElmDevice()
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun connectSelectedDevice(
        device: ElmDevice,
        token: String
    ) {

        trackingRepository.connectSelectedDevice(device, token)
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}