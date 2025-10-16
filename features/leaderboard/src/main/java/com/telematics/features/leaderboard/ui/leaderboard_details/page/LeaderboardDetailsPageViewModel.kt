package com.telematics.features.leaderboard.ui.leaderboard_details.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.common.extension.setLiveDataForResult
import com.telematics.core.data.repository.LeaderboardRepository
import com.telematics.core.model.leaderboard.LeaderboardMemberData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class LeaderboardDetailsPageViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    fun getUserListByType(type: Int): LiveData<Result<List<LeaderboardMemberData>>> {

        val leaderboardUserListState = MutableLiveData<Result<List<LeaderboardMemberData>>>()
        flow {
            val data = leaderboardRepository.getLeaderboardUserList(type)
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(leaderboardUserListState)
            .launchIn(viewModelScope)

        return leaderboardUserListState
    }
}