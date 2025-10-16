plugins {
    alias(libs.plugins.telematics.android.library)
    alias(libs.plugins.telematics.android.hilt)
}


android {
    namespace = "com.telematics.core.logger"
}

dependencies {
    implementation(projects.core.database)
}