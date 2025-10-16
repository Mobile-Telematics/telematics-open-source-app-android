@file:Suppress("DEPRECATION")

package com.telematics.core.datastore

import android.content.Context
import android.content.SharedPreferences
import com.telematics.core.common.di.DeprecatedSharedPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

@Deprecated("Migrated to PreferenceStorage")
class AuthHolderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:DeprecatedSharedPrefs private val sharedPreferences: SharedPreferences,
) : AuthHolder {

    private val AUTH_DATA = "auth_data"
    private val KEY_TOKEN = "ad_token"
    private val KEY_DEVICE_TOKEN = "device_token"
    private val KEY_FIREBASE_TOKEN = "firebase_token"
    private val KEY_ON_BOARDING_SHOWING = "onboarding_showing"
    private val KEY_REG_TYPE = "registration_type"
    private val KEY_USERID = "userId"
    private val KEY_LAST_USERID = "last_userId"
    private val KEY_NICKNAME = "nickname"
    private val KEY_FIRSTNAME = "firstname"
    private val KEY_LASTNAME = "lastname"
    private val KEY_AVATAR_PATH = "avatar_path"
    private val KEY_TELEMATICS_ON = "telematics_on"
    private val KEY_REFERRAL_URL = "referral_url"
    private val KEY_PROFILE_COMPLETED = "profile_completed"
    private val KEY_DRV_LICENCE_COMPLETED = "drv_licence_completed"
    private val KEY_CAR_DOCS_COMPLETED = "car_docs_completed"
    private val KEY_WIZARD_COMPLETED = "wizard_completed"
    private val KEY_WIZARD_SHOWN = "wizard_shown"
    private val KEY_ACCOUNT_COMPLETED_POPUP_SHOWN = "account_completed_popup_shown"
    private val KEY_ACCOUNT_VERIFIED = "account_verified"
    private val KEY_ACCOUNT_COMPLETED = "account_completed"
    private val KEY_REWARD_SHOWING = "reward_showing"
    private val KEY_REFRESH_TOKEN = "refresh_token"
    private val KEY_EXPIRES_IN = "expires_in"
    private val KEY_logoutByWrongJWT = "logoutByWrongJWT"
    private val KEY_enable_logged = "KEY_enable_logged"

    override var token: String?
        get() = sharedPreferences.getString(KEY_TOKEN, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_TOKEN, value) }
        }

    override var firebaseToken: String?
        get() = sharedPreferences.getString(KEY_FIREBASE_TOKEN, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_FIREBASE_TOKEN, value) }
        }

    override var deviceToken: String?
        get() = sharedPreferences.getString(KEY_DEVICE_TOKEN, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_DEVICE_TOKEN, value) }
        }

    override var onBoardingShowing: Boolean?
        get() = sharedPreferences.getBoolean(KEY_ON_BOARDING_SHOWING, false)
        set(value) {
            sharedPreferences.edit {
                putBoolean(
                    KEY_ON_BOARDING_SHOWING, value
                        ?: false
                )
            }
        }

    override var registrationType: String?
        get() = sharedPreferences.getString(KEY_REG_TYPE, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_REG_TYPE, value) }
        }


    override var lastUserToken: String?
        get() = sharedPreferences.getString(KEY_LAST_USERID, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_LAST_USERID, value) }
        }

    override var nickname: String?
        get() = sharedPreferences.getString(KEY_NICKNAME, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_NICKNAME, value) }
        }

    override var firstname: String?
        get() = sharedPreferences.getString(KEY_FIRSTNAME, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_FIRSTNAME, value) }
        }

    override var lastname: String?
        get() = sharedPreferences.getString(KEY_LASTNAME, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_LASTNAME, value) }
        }

    override var avatarPath: String?
        get() = sharedPreferences.getString(KEY_AVATAR_PATH, null)
        set(value) {
            sharedPreferences.edit {putString(KEY_AVATAR_PATH, value) }
        }

    override var telematicsOn: Boolean
        get() = sharedPreferences.getBoolean(KEY_TELEMATICS_ON, false)
        set(value) {
            sharedPreferences.edit {putBoolean(KEY_TELEMATICS_ON, value) }
        }

    override var referralLink: String?
        get() = sharedPreferences.getString(KEY_REFERRAL_URL, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_REFERRAL_URL, value) }
        }

    override var isProfileCompleted: Boolean
        get() = sharedPreferences.getBoolean(KEY_PROFILE_COMPLETED, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_PROFILE_COMPLETED, value) }
        }

    override var isDrivingLicenceCompleted: Boolean
        get() = sharedPreferences.getBoolean(KEY_DRV_LICENCE_COMPLETED, false)
        set(value) {
            sharedPreferences.edit {
                putBoolean(KEY_DRV_LICENCE_COMPLETED, value)
            }
        }

    override var isCarsDocsCompleted: Boolean
        get() = sharedPreferences.getBoolean(KEY_CAR_DOCS_COMPLETED, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_CAR_DOCS_COMPLETED, value) }
        }

    override var isWizardCompleted: Boolean
        get() = sharedPreferences.getBoolean(KEY_WIZARD_COMPLETED, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_WIZARD_COMPLETED, value) }
        }

    override var isWizardShown: Boolean
        get() = sharedPreferences.getBoolean(KEY_WIZARD_SHOWN, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_WIZARD_SHOWN, value) }
        }

    override var isAccountCompletedPopupShown: Boolean
        get() = sharedPreferences.getBoolean(KEY_ACCOUNT_COMPLETED_POPUP_SHOWN, false)
        set(value) {
            sharedPreferences.edit {
                putBoolean(KEY_ACCOUNT_COMPLETED_POPUP_SHOWN, value)
            }
        }

    override var isAccountVerified: Boolean
        get() = sharedPreferences.getBoolean(KEY_ACCOUNT_VERIFIED, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_ACCOUNT_VERIFIED, value) }
        }

    override var accountIsComplete: Boolean
        get() = sharedPreferences.getBoolean(KEY_ACCOUNT_COMPLETED, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_ACCOUNT_COMPLETED, value) }
        }

    override var rewardShowing: Boolean
        get() = sharedPreferences.getBoolean(KEY_REWARD_SHOWING, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_REWARD_SHOWING, value) }
        }

    override var refreshToken: String?
        get() = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_REFRESH_TOKEN, value) }
        }
    override var expiresIn: Long?
        get() = sharedPreferences.getLong(KEY_EXPIRES_IN, 0)
        set(value) {
            sharedPreferences.edit { putLong(KEY_EXPIRES_IN, value!!) }
        }

    override var blockchainNetworkSolanaId: String
        get() = sharedPreferences.getString("blockchainNetworkId", "") ?: ""
        set(value) {
            sharedPreferences.edit { putString("blockchainNetworkId", value) }
        }

    override var recipientWalletSolanaAddress: String
        get() = sharedPreferences.getString("recipientWalletAddress", "") ?: ""
        set(value) {
            sharedPreferences.edit {
                putString("recipientWalletAddress", value)
            }
        }

    override var blockchainTokenSolanaId: String
        get() = sharedPreferences.getString("blockchainTokenId", "") ?: ""
        set(value) {
            sharedPreferences.edit { putString("blockchainTokenId", value) }
        }

    private fun getSharedPreferences(prefName: String) =
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    override var logoutByWrongJWT: Boolean
        get() = sharedPreferences.getBoolean(KEY_logoutByWrongJWT, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_logoutByWrongJWT, value) }
        }

    override var isLoggedEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_enable_logged, false)
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_enable_logged, value) }
        }
}

@Deprecated("Migrated to PreferenceStorage")
interface AuthHolder {
    var onBoardingShowing: Boolean?
    var registrationType: String?

    var token: String?
    var deviceToken: String?
    var firebaseToken: String?
    var refreshToken: String?
    var expiresIn: Long?

    var lastUserToken: String?

    var nickname: String?
    var firstname: String?
    var lastname: String?
    var avatarPath: String?
    var telematicsOn: Boolean
    var referralLink: String?

    var accountIsComplete: Boolean
    var isProfileCompleted: Boolean
    var isDrivingLicenceCompleted: Boolean
    var isCarsDocsCompleted: Boolean
    var isWizardCompleted: Boolean
    var isWizardShown: Boolean
    var isAccountCompletedPopupShown: Boolean
    var isAccountVerified: Boolean

    var rewardShowing: Boolean

    var logoutByWrongJWT: Boolean

    var isLoggedEnabled: Boolean

    var blockchainNetworkSolanaId: String
    var recipientWalletSolanaAddress: String
    var blockchainTokenSolanaId: String
}