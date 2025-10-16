import com.telematics.AppConfig
import com.telematics.buildConfigString

plugins {
    alias(libs.plugins.telematics.android.feature)
}

android {
    namespace = "com.telematics.features.settings"

    defaultConfig {
        buildConfigString("PRIVACY_POLICY", AppConfig.PRIVACY_POLICY)
        buildConfigString("TERMS_OF_USE", AppConfig.TERMS_OF_USE)
        buildConfigString("VERSION", "${AppConfig.VERSION_NAME}.${AppConfig.VERSION_CODE}")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.features.obd)
}