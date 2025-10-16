package com.telematics.core.common.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor() : ViewModel() {

    private val _appNavigation: MutableStateFlow<AppNavigation> =
        MutableStateFlow(AppNavigation.Idle)
    val appNavigation: StateFlow<AppNavigation>
        get() = _appNavigation.asStateFlow()

    fun navigateTo(destination: AppNavigation) {
        viewModelScope.launch {
            _appNavigation.emit(destination)
        }
    }

    private val _editAvatar: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val editAvatar: SharedFlow<Boolean>
        get() = _editAvatar.asSharedFlow()

    fun editAvatar() {
        viewModelScope.launch {
            _editAvatar.emit(true)
        }
    }
}