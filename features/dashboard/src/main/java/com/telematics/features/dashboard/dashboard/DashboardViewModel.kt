package com.telematics.features.dashboard.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.telematics.core.common.provider.ApiEventsProvider
import com.telematics.core.data.repository.DashboardDataRepository
import com.telematics.core.data.repository.TrackingRepository
import com.telematics.core.data.repository.UserAuthRepository
import com.telematics.core.data.repository.UserDataRepository
import com.telematics.core.data.repository.VideoRepository
import com.telematics.core.domain.usecase.HasNetworkConnectionUseCase
import com.telematics.core.domain.usecase.LogoutUseCase
import com.telematics.core.model.TripRecordMode
import com.telematics.features.dashboard.DashboardConfig
import com.telematics.features.dashboard.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val videoRepository: VideoRepository,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase,
    private val dashboardDataRepository: DashboardDataRepository,
    private val trackingRepository: TrackingRepository,
    private val logoutUseCase: LogoutUseCase,
    private val userDataRepository: UserDataRepository,
    apiEventsProvider: ApiEventsProvider,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    val dashboardConfig = readDashboardConfigFromRaw(context)

    private val _tripRecordMode: MutableStateFlow<Pair<TripRecordMode, Boolean>?> =
        MutableStateFlow(null)
    val tripRecordMode: StateFlow<Pair<TripRecordMode, Boolean>?>
        get() = _tripRecordMode.asStateFlow()

    private val _videoLink: MutableSharedFlow<String?> = MutableSharedFlow()
    val videoLink: SharedFlow<String?>
        get() = _videoLink.asSharedFlow()

    private val _tripRecordModeUpdated: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val tripRecordModeUpdated: SharedFlow<Boolean>
        get() = _tripRecordModeUpdated.asSharedFlow()

    private val _logout: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val logout: SharedFlow<Boolean>
        get() = _logout.asSharedFlow()

    var deviceToken: String = ""
        private set

    fun isPermissionsGranted() = trackingRepository.checkPermissions()

    val isUnauthenticated = apiEventsProvider.unauthenticatedErrorEvent

    val isUnsupportedVersion = apiEventsProvider.unsupportedVersionErrorEvent

    val simpleModeFlow = userDataRepository.getSimpleModeFlow()

    init {
        checkAppFlags()
    }

    fun checkAppFlags() {
        viewModelScope.launch {
            trackingRepository.getTripRecordMode().run {
                onSuccess {
                    _tripRecordMode.emit(it)
                }
            }
        }
    }

    fun setTripRecordMode(mode: TripRecordMode, isActive: Boolean) {
        viewModelScope.launch {
            trackingRepository.setTripRecordMode(mode, isActive).run {
                onSuccess {
                    _tripRecordMode.update {
                        Pair(mode, isActive)
                    }

                    _tripRecordModeUpdated.emit(true)
                }
                onFailure {
                    _tripRecordModeUpdated.emit(false)
                }
            }
        }
    }

    fun switchTripRecordActiveMode() {
        viewModelScope.launch {
            tripRecordMode.value?.apply {
                trackingRepository.setTripRecordMode(first, !second).run {
                    onSuccess {
                        _tripRecordMode.update {
                            Pair(first, !second)
                        }
                    }
                    onFailure {
                        _tripRecordModeUpdated.emit(false)
                    }
                }
            }
        }
    }

    fun refreshDashboardData() {
        viewModelScope.launch {
            if (!hasNetworkConnectionUseCase()) {
                return@launch
            }

            val simpleMode = userDataRepository.getSimpleMode()

            with(dashboardConfig) {
                dashboardDataRepository.refreshDashboardData(
                    currentDailySafetyScore = !simpleMode && isScoreTrendEnabled(),
                    currentSafetyScore = isStatisticsEnabled(),
                    currentYearEcoScore = !simpleMode && isEcoScoringEnabled(),
                    latestTrip = !simpleMode && isLastScoredTripEnabled(),
                    leaderboard = !simpleMode && (isLeaderboardEnabled() || isRankEnabled()),
                    driveCoins = !simpleMode && isDriveCoinsEnabled(),
                    strakes = !simpleMode && isDrivingSrakesEnabled(),
                    video = !simpleMode && isVideoEnabled()
                )
            }
        }
    }

    fun getDashboardDataFlow() = dashboardDataRepository.getDashboardDataFlow()

    fun getVideoUrl(id: String) {
        viewModelScope.launch {
            videoRepository.getVideoUrl(id).run {
                onSuccess {
                    deviceToken = userAuthRepository.getDeviceToken()
                    _videoLink.emit(it)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _logout.emit(true)
        }
    }

    private fun readDashboardConfigFromRaw(context: Context): DashboardConfig {
        return try {
            context.resources.openRawResource(R.raw.dashboard_config).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    Gson().fromJson(reader, DashboardConfig::class.java)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DashboardConfig()
        }
    }
}