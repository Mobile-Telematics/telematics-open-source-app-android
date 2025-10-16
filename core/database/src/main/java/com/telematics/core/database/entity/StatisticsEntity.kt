package com.telematics.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statistics")
data class StatisticsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "trips_count")
    val tripsCount: Int,
    @ColumnInfo(name = "driving_time")
    val drivingTime: Double,
    @ColumnInfo(name = "mileage_km")
    val mileageKm: Double,
    @ColumnInfo(name = "user_id")
    val userId: String,
)
