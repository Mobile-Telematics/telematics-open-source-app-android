package com.telematics.core.data.repository

import android.app.Application
import android.util.Log
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.BuildConfig
import com.telematics.core.data.datasource.local.UserAuthLocalDataSource
import com.telematics.core.data.datasource.local.UserDataLocalDataSource
import com.telematics.core.data.datasource.local.UserProfileLocalDataSource
import com.telematics.core.model.intercom.IntercomUserModel
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.IntercomError
import io.intercom.android.sdk.IntercomSpace
import io.intercom.android.sdk.IntercomStatusCallback
import io.intercom.android.sdk.UserAttributes
import io.intercom.android.sdk.identity.Registration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class IntercomRepositoryImpl @Inject constructor(
    private val userAuthLocalDataSource: UserAuthLocalDataSource,
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userDataLocalDataSource: UserDataLocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : IntercomRepository {

    private val intercom = Intercom

    private lateinit var userAttributes: UserAttributes

    override fun initIntercom(application: Application) {
        intercom.initialize(
            application,
            BuildConfig.INTERCOM_API_KEY,
            BuildConfig.INTERCOM_APP_ID
        )
    }

    override suspend fun createIntercomUser(model: IntercomUserModel): Boolean =
        withContext(ioDispatcher) {
            suspendCoroutine { continuation ->
                userAttributes = UserAttributes.Builder()
                    .withUserId(model.userId)
                    .withName(model.userName)
                    .withEmail(model.userEmail)
                    .withCustomAttribute("Source", BuildConfig.SOURCE)
                    .build()

                val registration = Registration.create().withUserId(model.userId)

                intercom.client().loginIdentifiedUser(
                    userRegistration = registration,
                    intercomStatusCallback = object : IntercomStatusCallback {
                        override fun onSuccess() {
                            Log.i("IntercomLogin", "Success")

                            Intercom.client().handlePushMessage()

                            continuation.resume(true)
                        }

                        override fun onFailure(intercomError: IntercomError) {
                            Log.i("IntercomLogin", "error message ${intercomError.errorMessage}")
                            continuation.resume(false)
                        }
                    }
                )
            }
        }

    override suspend fun createIntercomCurrentUser(): Boolean =
        withContext(ioDispatcher) {
            val intercomUserModel = IntercomUserModel(
                userId = userAuthLocalDataSource.deviceToken,
                userEmail = userProfileLocalDataSource.userProfile.email,
                userName = userProfileLocalDataSource.userProfile.fullName,
                phone = userProfileLocalDataSource.userProfile.phoneNumber
            )

            if (createIntercomUser(intercomUserModel) && updateUser(intercomUserModel)) {
                userDataLocalDataSource.intercomUserModel = intercomUserModel
                true
            } else {
                false
            }
        }

    override suspend fun updateIntercomUser() {
        withContext(ioDispatcher) {
            val intercomUserModel = IntercomUserModel(
                userId = userAuthLocalDataSource.deviceToken,
                userEmail = userProfileLocalDataSource.userProfile.email,
                userName = userProfileLocalDataSource.userProfile.fullName,
                phone = userProfileLocalDataSource.userProfile.phoneNumber
            )

            if (userDataLocalDataSource.intercomUserModel != intercomUserModel) {

                val needLogout =
                    userDataLocalDataSource.intercomUserModel.userId != intercomUserModel.userId

                userDataLocalDataSource.intercomUserModel = intercomUserModel

                if (needLogout) {
                    logout()
                    if (createIntercomUser(intercomUserModel) && updateUser(intercomUserModel)) {
                        userDataLocalDataSource.intercomUserModel = intercomUserModel
                    }
                } else {
                    if (updateUser(intercomUserModel)) {
                        userDataLocalDataSource.intercomUserModel = intercomUserModel
                    }
                }
            }
        }
    }


    override suspend fun updateUser(user: IntercomUserModel): Boolean =
        withContext(ioDispatcher) {
            suspendCoroutine { continuation ->
                userAttributes = UserAttributes.Builder()
                    .withUserId(user.userId)
                    .withName(user.userName)
                    .withEmail(user.userEmail)
                    .withPhone(user.phone)
                    .withCustomAttribute("Source", BuildConfig.SOURCE)
                    .build()

                Intercom.client().updateUser(
                    userAttributes = userAttributes,
                    intercomStatusCallback = object : IntercomStatusCallback {
                        override fun onSuccess() {
                            Log.d("IntercomUserUpdate", "user update success")
                            continuation.resume(true)
                        }

                        override fun onFailure(intercomError: IntercomError) {
                            Log.e(
                                "IntercomUserUpdate",
                                "error message ${intercomError.errorMessage}"
                            )
                            continuation.resume(false)
                        }
                    }
                )
            }
        }

    override fun showIntercomHomeSpace() {
        intercom.client().present(IntercomSpace.Home)
    }


    override fun logout() {
        intercom.client().logout()
    }
}

interface IntercomRepository {
    fun initIntercom(application: Application)
    suspend fun createIntercomUser(model: IntercomUserModel): Boolean
    suspend fun createIntercomCurrentUser(): Boolean
    suspend fun updateIntercomUser()
    suspend fun updateUser(user: IntercomUserModel): Boolean
    fun showIntercomHomeSpace()
    fun logout()
}