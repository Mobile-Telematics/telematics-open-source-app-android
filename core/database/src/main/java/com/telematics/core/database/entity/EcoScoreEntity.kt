package com.telematics.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eco_score")
data class EcoScoreEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "score")
    val score: Int = 0,
    @ColumnInfo(name = "fuel")
    val fuel: Int = 0,
    @ColumnInfo(name = "tires")
    val tires: Int = 0,
    @ColumnInfo(name = "brakes")
    val brakes: Int = 0,
    @ColumnInfo(name = "cost")
    val cost: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: String,
) {
}