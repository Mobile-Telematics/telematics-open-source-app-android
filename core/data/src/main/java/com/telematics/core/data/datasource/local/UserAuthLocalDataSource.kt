@file:Suppress("DEPRECATION")

package com.telematics.core.data.datasource.local

import com.telematics.core.datastore.AuthHolder
import com.telematics.core.datastore.PreferenceStorage
import com.telematics.core.model.SessionData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAuthLocalDataSourceImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val authHolder: AuthHolder
) : UserAuthLocalDataSource {

    override var userEmail
        get() = preferenceStorage.userEmail
        set(value) {
            preferenceStorage.userEmail = value
        }

    override var session: SessionData
        get() = SessionData(
            accessToken = preferenceStorage.accessToken,
            refreshToken = preferenceStorage.refreshToken,
        )
        set(value) {
            preferenceStorage.accessToken = value.accessToken
            preferenceStorage.refreshToken = value.refreshToken
        }

    override var accessToken
        get() = preferenceStorage.accessToken
        set(value) {
            preferenceStorage.accessToken = value
        }

    override var refreshToken
        get() = preferenceStorage.refreshToken
        set(value) {
            preferenceStorage.refreshToken = value
        }

    override var rewardInviteState: Boolean
        get() = preferenceStorage.rewardInviteState
        set(value) {
            preferenceStorage.rewardInviteState = value
        }

    override var deviceToken
        get() = preferenceStorage.deviceToken
        set(value) {
            preferenceStorage.deviceToken = value
        }
    override var firebaseId
        get() = preferenceStorage.firebaseId
        set(value) {
            preferenceStorage.firebaseId = value
        }

    override var userId
        get() = preferenceStorage.userId
        set(value) {
            preferenceStorage.userId = value
        }

    override fun migrateAuthData() {
        accessToken = authHolder.token ?: ""
        refreshToken = authHolder.refreshToken ?: ""
        rewardInviteState = authHolder.rewardShowing
        deviceToken = authHolder.deviceToken ?: ""
    }
}

interface UserAuthLocalDataSource {
    var userEmail: String
    var accessToken: String
    var refreshToken: String

    var session: SessionData
    var rewardInviteState: Boolean

    var deviceToken: String
    var firebaseId: String
    var userId: String?

    fun migrateAuthData()
}