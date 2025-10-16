package com.telematics.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "time_start")
    val timeStart: String,
    @ColumnInfo(name = "time_end")
    val timeEnd: String,
    @ColumnInfo(name = "dist")
    val dist: Float,
    @ColumnInfo(name = "street_start")
    val streetStart: String,
    @ColumnInfo(name = "street_end")
    val streetEnd: String,
    @ColumnInfo(name = "city_start")
    val cityStart: String,
    @ColumnInfo(name = "city_end")
    val cityEnd: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "is_origin_changed")
    val isOriginChanged: Boolean,
    @ColumnInfo(name = "user_id")
    val userId: String,
)
