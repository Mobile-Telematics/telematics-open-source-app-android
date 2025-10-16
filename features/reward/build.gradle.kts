plugins {
    alias(libs.plugins.telematics.android.feature)
}

android {
    namespace = "com.telematics.features.reward"
}

dependencies {
    implementation(libs.mpAndroidChart)
}
