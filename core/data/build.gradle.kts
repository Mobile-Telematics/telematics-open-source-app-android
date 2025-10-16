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
    namespace = "com.telematics.core.data"

    defaultConfig {
        buildConfigString("INTERCOM_APP_ID", AppConfig.INTERCOM_APP_ID)
        buildConfigString("INTERCOM_API_KEY", AppConfig.INTERCOM_API_KEY)

        buildConfigString("SOURCE", AppConfig.SOURCE)
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
    implementation(projects.core.network)

    implementation(projects.core.database)
    implementation(projects.core.datastore)

    implementation(libs.javax.inject)
    implementation(libs.javax.annotation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment)

    implementation(libs.telematicssdk.tracking)

    // Intercom
    implementation(libs.intercom.sdk)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}