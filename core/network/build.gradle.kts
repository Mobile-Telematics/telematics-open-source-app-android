import com.telematics.AppConfig
import com.telematics.buildConfigString
import java.util.Properties

plugins {
    alias(libs.plugins.telematics.android.library)
    alias(libs.plugins.telematics.android.room)
    alias(libs.plugins.telematics.android.retrofit)
    alias(libs.plugins.telematics.android.hilt)
}

android {
    namespace = "com.telematics.core.network"

    defaultConfig {
        buildConfigString("X_API_KEY", AppConfig.X_API_KEY)
        buildConfigString("USER_SERVICE_URL", AppConfig.USER_SERVICE_URL)
        buildConfigString("DRIVE_COINS_URL", AppConfig.DRIVE_COINS_URL)
        buildConfigString("USER_STATISTICS_URL", AppConfig.USER_STATISTICS_URL)
        buildConfigString("LEADERBOARD_URL", AppConfig.LEADERBOARD_URL)
        buildConfigString("OPENSOURCE_URL", AppConfig.OPENSOURCE_URL)
        buildConfigString("TRIP_EVENT_TYPE_URL", AppConfig.TRIP_EVENT_TYPE_URL)
        buildConfigString("CAR_SERVICE_URL", AppConfig.CAR_SERVICE_URL)
    }

    buildTypes {
        getByName("release") {

        }
        getByName("debug") {

        }
    }


    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.content)
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.datastore)

    implementation(libs.telematicssdk.tracking)
}