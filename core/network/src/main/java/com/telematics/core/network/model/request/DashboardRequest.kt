package com.telematics.core.network.model.request


import com.google.gson.annotations.SerializedName

data class DashboardRequest(
    @SerializedName("services")
    val services: Services
) {
    data class Services(
        @SerializedName("current_daily_safety_score")
        val currentDailySafetyScore: Boolean,
        @SerializedName("current_safety_score")
        val currentSafetyScore: Boolean,
        @SerializedName("current_year_eco_score")
        val currentYearEcoScore: Boolean,
        @SerializedName("current_year_statistics")
        val currentYearStatistics: Boolean,
        @SerializedName("drivecoins")
        val drivecoins: Boolean,
        @SerializedName("last_month_statistics")
        val lastMonthStatistics: Boolean,
        @SerializedName("last_week_statistics")
        val lastWeekStatistics: Boolean,
        @SerializedName("last_year_statistics")
        val lastYearStatistics: Boolean,
        @SerializedName("latest_trip")
        val latestTrip: Boolean,
        @SerializedName("leaderboard")
        val leaderboard: Boolean
    )
}