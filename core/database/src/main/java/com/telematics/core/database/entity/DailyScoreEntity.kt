package com.telematics.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_score")
data class DailyScoreEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "score")
    val score: Int,
    @ColumnInfo(name = "calc_date")
    val calcDate: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
)
