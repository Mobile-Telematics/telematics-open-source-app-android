package com.telematics.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drive_coins")
data class DriveCoinsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "drive_coins")
    val driveCoins: Int,
    @ColumnInfo(name = "user_id")
    val userId: String,
)
