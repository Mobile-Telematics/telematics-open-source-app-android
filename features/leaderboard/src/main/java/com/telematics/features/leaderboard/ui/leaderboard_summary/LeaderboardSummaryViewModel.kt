package com.telematics.features.leaderboard.ui.leaderboard_summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.core.data.repository.LeaderboardRepository
import com.telematics.core.domain.usecase.HasNetworkConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardSummaryViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
    private val hasNetworkConnectionUseCase: HasNetworkConnectionUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<LeaderboardSummaryUiState> =
        MutableStateFlow(LeaderboardSummaryUiState())
    val uiState: StateFlow<LeaderboardSummaryUiState>
        get() = _uiState.asStateFlow()

    fun refreshLeaderboardDataData() {
        viewModelScope.launch {
            if (!hasNetworkConnectionUseCase()) {
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            leaderboardRepository.refreshLeaderboardData().run {
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    fun getLeaderboardDataFlow() = leaderboardRepository.getLeaderboardDataFlow()
}