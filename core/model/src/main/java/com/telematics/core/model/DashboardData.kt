package com.telematics.core.model

import com.telematics.core.model.statistics.DailyScore
import com.telematics.core.model.statistics.EcoScore
import com.telematics.core.model.statistics.Score
import com.telematics.core.model.video.VideoData

data class DashboardData(
    val tripsCount: Int = 0,
    val mileageKm: Double = .0,
    val drivingTime: Double = .0,
    val lastTrip: Trip? = null,
    val place: Int? = null,
    val totalDrivers: Int? = null,
    val dailyScores: List<DailyScore> = listOf(),
    val scores: List<Score> = Score.empty(),
    val ecoScore: EcoScore = EcoScore(),
    val videoData: VideoData? = null,
    val driveCoins: Int? = null,
    val drivingStrakes: Any? = null
)
