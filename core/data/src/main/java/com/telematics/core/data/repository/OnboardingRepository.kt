package com.telematics.core.data.repository

import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.datasource.local.OnboardingLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val onboardingLocalDataSource: OnboardingLocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : OnboardingRepository {
    override suspend fun needOnboarding(): Result<Boolean> =
        withContext(ioDispatcher) {
            try {
                Result.success(onboardingLocalDataSource.needOnboarding)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    override suspend fun setNeedOnboarding(need: Boolean) {
        withContext(ioDispatcher) {
            try {
                onboardingLocalDataSource.needOnboarding = need
            } catch (_: Exception) {

            }
        }

    }

}

interface OnboardingRepository {
    suspend fun needOnboarding(): Result<Boolean>
    suspend fun setNeedOnboarding(need: Boolean)
}