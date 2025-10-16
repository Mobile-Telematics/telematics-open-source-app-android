package com.telematics.core.common.extension

import com.telematics.core.common.R
import com.telematics.core.model.leaderboard.LeaderboardType

fun LeaderboardType.getIconRes(): Int = when (this) {
    LeaderboardType.Rate -> 0
    LeaderboardType.Acceleration -> R.drawable.ic_leaderboard_acceleration
    LeaderboardType.Deceleration -> R.drawable.ic_leaderboard_deceleration
    LeaderboardType.Speeding -> R.drawable.ic_leaderboard_speeding
    LeaderboardType.Distraction -> R.drawable.ic_leaderboard_phone
    LeaderboardType.Turn -> R.drawable.ic_leaderboard_cornering
    LeaderboardType.Trips -> R.drawable.ic_leaderboard_trips
    LeaderboardType.Distance -> R.drawable.ic_leaderboard_mileage
    LeaderboardType.Duration -> R.drawable.ic_leaderboard_time
}

fun LeaderboardType.getStringRes(): Int = when (this) {
    LeaderboardType.Rate -> R.string.leaderboard_rate
    LeaderboardType.Acceleration -> R.string.leaderboard_acceleration
    LeaderboardType.Deceleration -> R.string.leaderboard_deceleration
    LeaderboardType.Speeding -> R.string.leaderboard_speeding
    LeaderboardType.Distraction -> R.string.leaderboard_distraction
    LeaderboardType.Turn -> R.string.leaderboard_turn
    LeaderboardType.Trips -> R.string.leaderboard_total_trips
    LeaderboardType.Distance -> R.string.leaderboard_mileage
    LeaderboardType.Duration -> R.string.leaderboard_time_driven
}