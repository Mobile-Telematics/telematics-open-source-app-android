package com.telematics.core.data.repository

import com.telematics.core.datastore.PreferenceStorage
import com.telematics.core.model.SessionData
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : SessionRepository {
    override fun saveSession(session: SessionData) {
        preferenceStorage.accessToken = session.accessToken
        preferenceStorage.refreshToken = session.refreshToken
    }

    override fun getSession(): SessionData {
        return SessionData(
            accessToken = preferenceStorage.accessToken,
            refreshToken = preferenceStorage.refreshToken,
        )
    }

    override fun clearSession() {
        preferenceStorage.accessToken = ""
        preferenceStorage.refreshToken = ""
    }

    override fun saveStateForRewardInviteScreen() {
        preferenceStorage.rewardInviteState = true
    }

    override fun isRewardInviteScreenOpened(): Boolean {
        return preferenceStorage.rewardInviteState
    }

    override fun clearStateForRewardInviteScreen() {
        preferenceStorage.rewardInviteState = false
    }
}

interface SessionRepository {
    fun saveSession(session: SessionData)
    fun getSession(): SessionData
    fun clearSession()

    fun saveStateForRewardInviteScreen()
    fun isRewardInviteScreenOpened(): Boolean
    fun clearStateForRewardInviteScreen()
}