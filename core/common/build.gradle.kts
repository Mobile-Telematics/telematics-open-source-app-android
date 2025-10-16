import com.telematics.AppConfig
import com.telematics.buildConfigString
import java.util.Properties

plugins {
    alias(libs.plugins.telematics.android.library)
    alias(libs.plugins.telematics.android.hilt)
}

android {
    namespace = "com.telematics.core.common"

    defaultConfig {
        buildConfigString("MUX_DATA_EVENT_KEY", AppConfig.MUX_DATA_EVENT_KEY)
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.content)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.fragment.ktx)

    implementation(libs.google.material)

    implementation(libs.glide)
    ksp(libs.glide.ksp)

    implementation(libs.android.image.cropper)

    // ExoPlayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // MuxPlayer
    implementation(libs.muxplayer)
}