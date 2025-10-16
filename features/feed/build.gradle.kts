import com.telematics.AppConfig
import java.util.Properties

plugins {
    alias(libs.plugins.telematics.android.feature)
}

android {
    namespace = "com.telematics.features.feed"

    defaultConfig {
        manifestPlaceholders["MAPS_API_KEY"] = AppConfig.GOOGLE_MAP_API
    }
}

dependencies {
    implementation(libs.google.play.services.maps)
    implementation(libs.intercom.sdk)
}
