package com.telematics.core.common.navigation

sealed class AppNavigation {
    data object Idle : AppNavigation()
    data object TripsScreen : AppNavigation()
    data object LeaderboardScreen : AppNavigation()
    data object DashboardScreen : AppNavigation()
    data object RewardScreen : AppNavigation()
    data object RewardStreaksScreen : AppNavigation()
    data object AccountScreen : AppNavigation()
    data object SettingsScreen : AppNavigation()
    data object SplashScreen : AppNavigation()
}