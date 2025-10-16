import com.telematics.AppConfig
import com.telematics.buildConfigString

plugins {
    alias(libs.plugins.telematics.android.feature)
}


android {
    namespace = "com.telematics.features.login"

    defaultConfig {
        buildConfigString("PRIVACY_POLICY", AppConfig.PRIVACY_POLICY)
        buildConfigString("TERMS_OF_USE", AppConfig.TERMS_OF_USE)
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.countryCodePicker)
}