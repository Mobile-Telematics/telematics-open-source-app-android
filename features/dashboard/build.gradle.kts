import com.telematics.AppConfig
import com.telematics.buildConfigInteger

plugins {
    alias(libs.plugins.telematics.android.feature)
}

android {
    namespace = "com.telematics.features.dashboard"

    defaultConfig {
        buildConfigInteger("DASHBOARD_DISTANCE_LIMIT", AppConfig.DASHBOARD_DISTANCE_LIMIT)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.network)

    implementation(libs.google.gson)

    implementation(libs.mpAndroidChart)
    implementation(libs.circleIndicatorView)
    implementation(libs.dotsindicator)
}