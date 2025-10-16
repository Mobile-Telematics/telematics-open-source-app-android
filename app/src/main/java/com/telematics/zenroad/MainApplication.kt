package com.telematics.zenroad

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.telematics.core.data.repository.IntercomRepository
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.data.repository.UserDataRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var trackingRepository: TrackingRepository

    @Inject
    lateinit var intercomRepository: IntercomRepository

    @Inject
    lateinit var userDataRepository: UserDataRepository

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        userDataRepository.migrateSharedPrefs()

        trackingRepository.initializeSdk()
        intercomRepository.initIntercom(this)
    }
}