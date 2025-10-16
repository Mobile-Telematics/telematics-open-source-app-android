package com.telematics.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard_data")
data class LeaderboardDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "progress")
    val progress: Double,
    @ColumnInfo(name = "place")
    val place: Int,
    @ColumnInfo(name = "progress_max")
    val progressMax: Int,
    @ColumnInfo(name = "position")
    val position: Int,
    @ColumnInfo(name = "user_id")
    val userId: String,
)
