plugins {
    alias(libs.plugins.telematics.android.feature)
}

android {
    namespace = "com.telematics.features.account"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.countryCodePicker)
}