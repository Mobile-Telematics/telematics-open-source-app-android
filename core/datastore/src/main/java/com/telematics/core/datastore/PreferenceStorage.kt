package com.telematics.core.datastore

import android.content.Context
import android.content.SharedPreferences
import com.telematics.core.common.di.SharedPrefs
import com.telematics.core.encryption.Encryption
import com.telematics.core.model.TripRecordMode
import com.telematics.core.model.measures.DateMeasure
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.core.model.measures.MapStyle
import com.telematics.core.model.measures.TimeMeasure
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceStorage @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @SharedPrefs sharedPreferences: SharedPreferences,
    encryption: Encryption
) {
    var userEmail by sharedPreferences.string(
        key = { "jarot9zKfQgWWtK" },
        defaultValue = ""
    )

    var accessToken by sharedPreferences.string(
        key = { "access_token" },
    )

    var refreshToken by sharedPreferences.string(
        key = { "refresh_token" },
    )

    var deviceToken by sharedPreferences.string(
        key = { "device_token" },
    )

    var firebaseId by sharedPreferences.stringEncrypted(
        context = context,
        key = { "wlwF9qrt9bld90k" },
        encryption = encryption
    )

    /*------------- Session ----------------------*/

    var rewardInviteState by sharedPreferences.boolean(
        defaultValue = false,
        key = { "reward_invite_state" }
    )

    /*-----------------------------------*/

    var userId by sharedPreferences.stringNullable(
        key = { "user_id" },
        defaultValue = null
    )

    val id: String
        get() = userId ?: deviceToken

    /*------------- Profile ----------------------*/
    var profileUserId: String? by sharedPreferences.stringNullable(
        key = { "userId" },
        defaultValue = null
    )
    var profileFirstName: String? by sharedPreferences.stringNullable(
        key = { "firstName" },
        defaultValue = null
    )
    var profileLastName: String? by sharedPreferences.stringNullable(
        key = { "lastName" },
        defaultValue = null
    )
    var profileEmail: String? by sharedPreferences.stringNullable(
        key = { "email" },
        defaultValue = null
    )
    var profilePhoneNumber: String? by sharedPreferences.stringNullable(
        key = { "phone" },
        defaultValue = null
    )
    var profileBirthday: String? by sharedPreferences.stringNullable(
        key = { "birthday" },
        defaultValue = null
    )
    var profileClientId: String? by sharedPreferences.stringNullable(
        key = { "clientId" },
        defaultValue = null
    )
    var profileAdress: String? by sharedPreferences.stringNullable(
        key = { "address" },
        defaultValue = null
    )
    var profileDeviceToken: String? by sharedPreferences.stringNullable(
        key = { "deviceToken" },
        defaultValue = null
    )
    var profilePictureUrl: String? by sharedPreferences.stringNullable(
        key = { "profilePictureUrl" },
        defaultValue = null
    )
    var profileGender: String? by sharedPreferences.stringNullable(
        key = { "gender" },
        defaultValue = null
    )
    var profileMaritalStatus: String? by sharedPreferences.stringNullable(
        key = { "maritalStatus" },
        defaultValue = null
    )
    var profileChildrenCount: Int by sharedPreferences.int(
        key = { "childrenCount" },
        defaultValue = 0
    )

    var profileImage: String? by sharedPreferences.stringNullable(
        key = { "profile_image" },
        defaultValue = null
    )

    var simpleMode: Boolean by sharedPreferences.boolean(
        key = { "simple_mode" },
        defaultValue = false
    )

    /*------------ Onboarding -----------------------*/
    var needOnboarding: Boolean by sharedPreferences.boolean(
        key = { "needOnboarding" },
        defaultValue = true
    )

    /*--------------- Intercom --------------------*/
    var isIntercomRegister by sharedPreferences.boolean(
        defaultValue = false,
        key = { "intercom_is_register" }
    )
    var intercomUser by sharedPreferences.string(
        defaultValue = "",
        key = { "intercom_user" }
    )

    /*--------------- Other --------------------*/
    var helpScreen by sharedPreferences.string(
        defaultValue = "",
        key = { "help_screen" }
    )
    var mapState by sharedPreferences.boolean(
        defaultValue = false,
        key = { "map_state" }
    )


    var dateMeasure by sharedPreferences.string(
        defaultValue = DateMeasure.default.value,
        key = { "dateMeasureKey" }
    )
    var distanceMeasure by sharedPreferences.string(
        defaultValue = DistanceMeasure.default.value,
        key = { "distanceMeasureKey" }
    )
    var timeMeasure by sharedPreferences.string(
        defaultValue = TimeMeasure.default.value,
        key = { "timeMeasureKey" }
    )
    var mapStyle by sharedPreferences.string(
        defaultValue = MapStyle.default.value,
        key = { "mapStyleKey" }
    )

    var version by sharedPreferences.string(
        defaultValue = "",
        key = { "currentVersion" }
    )

    var notificationPermissions by sharedPreferences.boolean(
        defaultValue = !BuildConfig.REQUEST_NOTIFICATION_PERMISSION,
        key = { "notificationPermissionsKey" }
    )

    var sharedPrefsMigrated by sharedPreferences.boolean(
        defaultValue = false,
        key = { "sharedPrefsMigratedKey" }
    )

    var tripRecordMode by sharedPreferences.string(
        defaultValue = TripRecordMode.ALWAYS_ON.name,
        key = { "tripRecordModeKey" }
    )

    var isTripRecordModeActive by sharedPreferences.boolean(
        defaultValue = false,
        key = { "isTripRecordModeActive" }
    )
}

