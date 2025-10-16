package com.telematics.core.domain.usecase

import com.telematics.core.data.repository.IntercomRepository
import com.telematics.core.data.repository.SessionRepository
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.data.repository.UserDataRepository
import com.telematics.core.data.repository.UserProfileRepository
import com.telematics.core.model.TripRecordMode
import javax.inject.Inject

@Suppress("DEPRECATION")
open class LogoutUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val sessionRepository: SessionRepository,
    private val userProfileRepository: UserProfileRepository,
    private val intercomRepository: IntercomRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke() {
        trackingRepository.setTripRecordMode(TripRecordMode.ALWAYS_ON, false)
        sessionRepository.clearSession()
        userProfileRepository.clearUserProfile()
        userDataRepository.clearSimpleMode()
        intercomRepository.logout()
        trackingRepository.logout()
        sessionRepository.clearStateForRewardInviteScreen()
    }
}