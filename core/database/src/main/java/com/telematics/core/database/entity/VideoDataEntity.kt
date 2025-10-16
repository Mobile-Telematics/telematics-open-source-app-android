package com.telematics.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_data")
data class VideoDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "body")
    val body: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "video_id")
    val videoID: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
)
