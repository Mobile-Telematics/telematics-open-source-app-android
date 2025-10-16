plugins {
    alias(libs.plugins.telematics.android.library)
}

android {
    namespace = "com.telematics.core.content"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.google.material)
}