package com.telematics.core.domain.usecase

import android.content.Context
import android.os.Build
import com.telematics.core.common.extension.isNotificationGranted
import com.telematics.core.data.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

open class NotificationPermissionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @param:ApplicationContext private val context: Context,
) {

    operator fun invoke(): Flow<Boolean> {
        return flow {
            emit(
                when {
                    isNotificationGranted(context)
                            || settingsRepository.isNotificationPermissionCompleted() -> {
                        false
                    }

                    Build.VERSION.SDK_INT >= 33 -> {
                        true
                    }

                    else -> {
                        false
                    }
                }
            )
        }
    }
}