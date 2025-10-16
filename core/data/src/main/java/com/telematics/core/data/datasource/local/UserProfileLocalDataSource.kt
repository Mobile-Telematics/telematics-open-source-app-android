@file:Suppress("DEPRECATION")

package com.telematics.core.data.datasource.local

import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.datastore.AuthHolder
import com.telematics.core.datastore.PreferenceStorage
import com.telematics.core.model.UserProfile
import com.telematics.core.model.getEmptyUserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileLocalDataSourceImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val authHolder: AuthHolder,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserProfileLocalDataSource {

    private val _cachedData = MutableStateFlow<UserProfile?>(null)

    init {
        CoroutineScope(ioDispatcher).launch {
            _cachedData.value = userProfile
        }
    }

    private var email
        get() = preferenceStorage.profileEmail
        set(value) {
            preferenceStorage.profileEmail = value
        }
    private var clientId
        get() = preferenceStorage.profileClientId
        set(value) {
            preferenceStorage.profileClientId = value
        }
    private var firstName
        get() = preferenceStorage.profileFirstName
        set(value) {
            preferenceStorage.profileFirstName = value
        }
    private var lastName
        get() = preferenceStorage.profileLastName
        set(value) {
            preferenceStorage.profileLastName = value
        }
    private var birthday
        get() = preferenceStorage.profileBirthday
        set(value) {
            preferenceStorage.profileBirthday = value
        }
    private var phoneNumber
        get() = preferenceStorage.profilePhoneNumber
        set(value) {
            preferenceStorage.profilePhoneNumber = value
        }

    private var profileImage
        get() = preferenceStorage.profileImage
        set(value) {
            preferenceStorage.profileImage = value
        }

    override var userProfile
        get() = UserProfile(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            clientId = clientId,
            birthday = birthday,
            profileImage = profileImage,
        )
        set(value) {
            email = value.email
            clientId = value.clientId
            firstName = value.firstName
            lastName = value.lastName
            birthday = value.birthday
            phoneNumber = value.phoneNumber
            profileImage = value.profileImage

            _cachedData.update { value }
        }

    override fun getUserProfileFlow(): Flow<UserProfile?> = _cachedData.asStateFlow()

    override suspend fun clearUserProfile() {
        userProfile = getEmptyUserProfile()
        _cachedData.update { userProfile }
    }

    override fun migrateUserProfileData() {
        firstName = authHolder.firstname
        lastName = authHolder.lastname

        profileImage = authHolder.avatarPath
    }
}

interface UserProfileLocalDataSource {
    var userProfile: UserProfile
    fun getUserProfileFlow(): Flow<UserProfile?>
    suspend fun clearUserProfile()

    fun migrateUserProfileData()
}