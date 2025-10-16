import com.telematics.AppConfig
import com.telematics.TelematicsFlavor
import com.telematics.buildConfigString

plugins {
    alias(libs.plugins.telematics.android.application)
    alias(libs.plugins.telematics.android.application.flavors)
    alias(libs.plugins.telematics.android.hilt)
    alias(libs.plugins.telematics.android.retrofit)
    alias(libs.plugins.telematics.android.application.firebase)
}

android {
    namespace = "com.telematics.zenroad"

    defaultConfig {
        versionCode = AppConfig.VERSION_CODE
        versionName = AppConfig.VERSION_NAME
        applicationId = AppConfig.APP_ID

        base.archivesName = "Zenroad-${versionName}.${versionCode}"

        buildConfigString("PRIVACY_POLICY", AppConfig.PRIVACY_POLICY)
        buildConfigString("TERMS_OF_USE", AppConfig.TERMS_OF_USE)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true
        }
    }

    productFlavors {
        getByName(TelematicsFlavor.dev.name) {
            
        }
        getByName(TelematicsFlavor.prod.name) {

        }
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = "debug"
            keyPassword = "123456"
            storeFile = file("../certificates/debug.jks")
            storePassword = "123456"
        }
        create("release") {
            //keyAlias = "key0"
            //keyPassword = "******"
            //storeFile = file("path_to_file/keystore.jks")
            //storePassword = "******"
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}


dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.core.content)
    implementation(projects.features.feed)
    implementation(projects.features.dashboard)
    implementation(projects.features.account)
    implementation(projects.features.leaderboard)
    implementation(projects.features.reward)
    implementation(projects.features.splash)
    implementation(projects.features.onboarding)
    implementation(projects.features.login)
    implementation(projects.features.settings)

    // telematics sdk
    implementation(libs.telematicssdk.tracking)

    // lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)


    // navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Intercom
    implementation(libs.intercom.sdk)

    // FCM
    implementation(libs.firebase.messaging)
}