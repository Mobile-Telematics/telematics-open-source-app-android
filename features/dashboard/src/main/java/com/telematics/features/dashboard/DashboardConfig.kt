package com.telematics.features.dashboard

import com.google.gson.annotations.SerializedName

data class DashboardConfig(
    @SerializedName("tripRecordModeEnabled")
    private val tripRecordModeEnabled: Boolean? = false,
    @SerializedName("statisticsEnabled")
    private val statisticsEnabled: Boolean? = false,
    @SerializedName("statisticsDriveCoinsEnabled")
    private val statisticsDriveCoinsEnabled: Boolean? = false,
    @SerializedName("statisticsRankEnabled")
    private val statisticsRankEnabled: Boolean? = false,
    @SerializedName("videoEnabled")
    private val videoEnabled: Boolean? = false,
    @SerializedName("scoreTrendEnabled")
    private val scoreTrendEnabled: Boolean? = false,
    @SerializedName("annualDrivingSummaryEnabled")
    private val annualDrivingSummaryEnabled: Boolean? = false,
    @SerializedName("ecoScoringEnabled")
    private val ecoScoringEnabled: Boolean? = false,
    @SerializedName("lastScoredTripEnabled")
    private val lastScoredTripEnabled: Boolean? = false,
    @SerializedName("drivingSrakesEnabled")
    private val drivingSrakesEnabled: Boolean? = false,
    @SerializedName("leaderboardEnabled")
    private val leaderboardEnabled: Boolean? = false,

    ) {
    fun isTripRecordModeEnabled() = tripRecordModeEnabled == true
    fun isStatisticsEnabled() = statisticsEnabled == true
    fun isDriveCoinsEnabled() = statisticsEnabled == true && statisticsDriveCoinsEnabled == true
    fun isRankEnabled() = statisticsEnabled == true && statisticsRankEnabled == true
    fun isVideoEnabled() = videoEnabled == true
    fun isScoreTrendEnabled() = scoreTrendEnabled == true
    fun isAnnualDrivingSummaryEnabled() = annualDrivingSummaryEnabled == true
    fun isEcoScoringEnabled() = ecoScoringEnabled == true
    fun isLastScoredTripEnabled() = lastScoredTripEnabled == true
    fun isDrivingSrakesEnabled() = drivingSrakesEnabled == true
    fun isLeaderboardEnabled() = leaderboardEnabled == true
}