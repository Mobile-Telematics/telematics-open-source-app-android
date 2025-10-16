package com.telematics.features.reward.drivecoins

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.extension.setLiveDataForResult
import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.data.repository.RewardRepository
import com.telematics.core.model.reward.DailyLimitData
import com.telematics.core.model.reward.DriveCoinsDetailedData
import com.telematics.core.model.reward.DriveCoinsDuration
import com.telematics.core.model.reward.DriveCoinsTotalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class DriveCoinsViewModel @Inject constructor(
    private val rewardRepository: RewardRepository,
    val measuresFormatter: MeasuresFormatter
) : ViewModel() {

    fun getTotalCoinsByDuration(duration: DriveCoinsDuration): LiveData<Result<DriveCoinsTotalData>> {

        val totalCoinsDataState = MutableLiveData<Result<DriveCoinsTotalData>>()
        flow {
            val data = rewardRepository.getTotalCoinsByDuration(duration)
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(totalCoinsDataState)
            .launchIn(viewModelScope)
        return totalCoinsDataState
    }

    fun getDailyLimit(): LiveData<Result<DailyLimitData>> {

        val state = MutableLiveData<Result<DailyLimitData>>()
        flow {
            val data = rewardRepository.getDailyLimit()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }

    fun getDaily(): LiveData<Result<List<Pair<Int, Int>>>> {

        val state = MutableLiveData<Result<List<Pair<Int, Int>>>>()
        flow {
            val data = rewardRepository.getDaily()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }

    fun getDetailedByDuration(duration: DriveCoinsDuration): LiveData<Result<DriveCoinsDetailedData>> {

        val state = MutableLiveData<Result<DriveCoinsDetailedData>>()
        flow {
            val data = rewardRepository.getDetailed(duration)
            data.travelingTotalSpeedingKm =
                measuresFormatter.getDistanceByKm(data.travelingTotalSpeedingKm).roundToInt()
            data.travelingMileageData =
                measuresFormatter.getDistanceByKm(data.travelingMileageData).roundToInt()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }
}