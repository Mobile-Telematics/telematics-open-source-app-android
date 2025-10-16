package com.telematics.features.reward.rewards

import androidx.lifecycle.ViewModel
import com.telematics.core.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RewardViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : ViewModel() {


    val isNeedShowRewardsInvite: Boolean
        get() {
            return !sessionRepository.isRewardInviteScreenOpened()
        }

    fun inviteScreenClosed() {

        sessionRepository.saveStateForRewardInviteScreen()
    }
}