plugins {
    alias(libs.plugins.telematics.android.feature)
}

android {
    namespace = "com.telematics.features.leaderboard"
}

dependencies {
    implementation(libs.glide)
}
