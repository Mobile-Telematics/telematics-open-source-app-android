plugins {
    alias(libs.plugins.telematics.android.feature)
}

android {
    namespace = "com.telematics.features.onboarding"
}

dependencies {
    implementation(libs.circleIndicatorView)
}