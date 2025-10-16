import com.telematics.AppConfig
import com.telematics.buildConfigInteger
import com.telematics.buildConfigLong
import com.telematics.buildConfigString

plugins {
    alias(libs.plugins.telematics.android.library)
    alias(libs.plugins.telematics.android.room)
    alias(libs.plugins.telematics.android.hilt)
}


android {
    namespace = "com.telematics.core.database"

    defaultConfig {
        buildConfigInteger("DATABASE_VERSION", AppConfig.DATABASE_VERSION)
        buildConfigString("DATABASE_NAME", AppConfig.DATABASE_NAME)
        buildConfigLong("LOG_KEEPING_PERIOD", AppConfig.LOG_KEEPING_PERIOD)
    }

    buildFeatures {
        buildConfig = true
    }
}