package com.telematics.core.domain.usecase

import android.content.Context
import com.telematics.core.common.extension.isExactAlarmGranted
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

open class AlarmPermissionUseCase @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    operator fun invoke(): Flow<Boolean> {
        return flow {
            emit(
                isExactAlarmGranted(context)
            )
        }
    }
}