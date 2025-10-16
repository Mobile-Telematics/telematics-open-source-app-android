package com.telematics

object AppConfig {
    const val APP_ID = "your_application_id"

    private const val VERSION_MAJOR = 2
    private const val VERSION_MEDIUM = 5
    private const val VERSION_MINOR = 3
    private const val BUILD_NUMBER = 0
    const val COMPILE_SDK = 36
    const val MIN_SDK = 23
    const val TARGET_SDK = 36


    const val VERSION_NAME = "$VERSION_MAJOR.$VERSION_MEDIUM.$VERSION_MINOR"
    const val VERSION_CODE =
        (10_000 * VERSION_MAJOR + 100 * VERSION_MEDIUM + VERSION_MINOR) * 1000 + BUILD_NUMBER


    const val DATABASE_VERSION = 2
    const val DATABASE_NAME = "telematics-app-db"

    const val LOG_KEEPING_PERIOD = 3_600_000L * 24L * 7L   // 7 days

    const val SOURCE = "Zenroad"

    const val GOOGLE_MAP_API = "YOUR_GOOGLE_MAP_API_KEY"
    const val X_API_KEY = "YOUR_X-API-KEY"

    const val INTERCOM_APP_ID = "YOUR_INTERCOM_APP_ID"
    const val INTERCOM_API_KEY = "YOUR_INTERCOM_API_KEY"

    const val MUX_DATA_EVENT_KEY = "YOUR_MUX_DATA_EVENT_KEY"

    const val PRIVACY_POLICY = "YOUR_PRIVACY_POLICY_LINK"
    const val TERMS_OF_USE = "YOUR_TERMS_OF_USE_LINK"

    const val DASHBOARD_DISTANCE_LIMIT = 10 //in km

    // Needs request for notification permission(Android 13+)
    const val REQUEST_NOTIFICATION_PERMISSION = true

    const val USER_SERVICE_URL = "https://user.telematicssdk.com/"
    const val DRIVE_COINS_URL = "https://mobilesdk.telematicssdk.com/api/rewarding/"
    const val USER_STATISTICS_URL = "https://api.telematicssdk.com/"
    const val LEADERBOARD_URL = "https://leaderboard.telematicssdk.com/"
    const val TRIP_EVENT_TYPE_URL = "https://mobilesdk.telematicssdk.com/mobilesdk/stage/"
    const val CAR_SERVICE_URL = "https://services.telematicssdk.com/api/carservice/"

    const val OPENSOURCE_URL = "https://opensource.telematicssdk.com/"
}