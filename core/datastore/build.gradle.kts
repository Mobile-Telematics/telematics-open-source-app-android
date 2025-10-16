import com.telematics.AppConfig
import com.telematics.buildConfigBoolean

plugins {
    alias(libs.plugins.telematics.android.library)
    alias(libs.plugins.telematics.android.hilt)
}

android {
    namespace = "com.telematics.core.datastore"

    defaultConfig {
        buildConfigBoolean(
            "REQUEST_NOTIFICATION_PERMISSION",
            AppConfig.REQUEST_NOTIFICATION_PERMISSION
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.encryption)
    implementation(projects.core.model)
    implementation(projects.core.common)
}