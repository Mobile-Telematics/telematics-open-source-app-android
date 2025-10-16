@file:Suppress("DEPRECATION")

package com.telematics.core.data.datasource.local

import com.telematics.core.datastore.AuthHolder
import com.telematics.core.datastore.PreferenceStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingLocalDataSourceImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val authHolder: AuthHolder
) : OnboardingLocalDataSource {

    override var needOnboarding
        get() = preferenceStorage.needOnboarding
        set(value) {
            preferenceStorage.needOnboarding = value
        }

    override fun migrateOnboardingData() {
        needOnboarding = !(authHolder.onBoardingShowing ?: false)
    }
}

interface OnboardingLocalDataSource {
    var needOnboarding: Boolean

    fun migrateOnboardingData()
}
