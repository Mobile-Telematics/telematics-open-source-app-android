package com.telematics.core.data.repository

import com.telematics.core.data.datasource.local.UserAuthLocalDataSource
import com.telematics.core.data.datasource.local.UserDataLocalDataSource
import com.telematics.core.data.datasource.local.UserProfileLocalDataSource
import com.telematics.core.model.UserProfile
import com.telematics.core.model.intercom.IntercomUserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val userAuthLocalDataSource: UserAuthLocalDataSource,
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userDataLocalDataSource: UserDataLocalDataSource
) : UserDataRepository {
    override suspend fun getIntercomUserModel(): IntercomUserModel =
        userDataLocalDataSource.intercomUserModel

    override fun getSavedMapState() = userDataLocalDataSource.mapState
    override fun migrateSharedPrefs() {
        if (!userDataLocalDataSource.sharedPrefsMigrated) {
            userAuthLocalDataSource.migrateAuthData()
            userProfileLocalDataSource.migrateUserProfileData()
            userDataLocalDataSource.migrateUserData()

            userDataLocalDataSource.sharedPrefsMigrated = true
        }
    }

    override fun getSimpleModeFlow(): Flow<Boolean> =
        userDataLocalDataSource.getSimpleModeFlow()

    override fun getSimpleMode(): Boolean = userDataLocalDataSource.simpleMode

    override suspend fun clearSimpleMode() = userDataLocalDataSource.clearSimpleMode()

    override suspend fun saveSimpleMode(mode: Boolean) {
        userDataLocalDataSource.simpleMode = mode
    }
}

interface UserDataRepository {
    suspend fun getIntercomUserModel(): IntercomUserModel
    fun getSavedMapState(): Boolean
    fun migrateSharedPrefs()

    fun getSimpleModeFlow(): Flow<Boolean>
    fun getSimpleMode(): Boolean
    suspend fun clearSimpleMode()
    suspend fun saveSimpleMode(mode: Boolean)
}