plugins {
    alias(libs.plugins.telematics.android.feature)
}

android {
    namespace = "com.telematics.features.obd"
}

dependencies {
    implementation(libs.circleIndicatorView)
}
