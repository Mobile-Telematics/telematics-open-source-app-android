package com.telematics.features.reward.streaks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.extension.setLiveDataForResult
import com.telematics.core.data.repository.RewardRepository
import com.telematics.core.model.reward.Streak
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class StreaksViewModel @Inject constructor(
    private val rewardRepository: RewardRepository
) : ViewModel() {

    fun getStreaksData(): LiveData<Result<List<Streak>>> {

        val streakState = MutableLiveData<Result<List<Streak>>>()
        flow {
            val data = rewardRepository.getStreaks()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(streakState)
            .launchIn(viewModelScope)
        return streakState
    }
}