plugins {
    alias(libs.plugins.telematics.android.library)
    alias(libs.plugins.telematics.android.room)
    id("kotlin-parcelize")
}

android {
    namespace = "com.telematics.core.model"
}

dependencies {
    implementation(projects.core.content)
    implementation(libs.androidx.recyclerview)
}