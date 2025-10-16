plugins {
    alias(libs.plugins.telematics.android.library)
    alias(libs.plugins.telematics.android.room)
    alias(libs.plugins.telematics.android.hilt)
}


android {
    namespace = "com.telematics.core.domain"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.network)

    implementation(libs.androidx.recyclerview)
    implementation(libs.telematicssdk.tracking)
}