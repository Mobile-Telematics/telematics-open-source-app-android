package com.telematics.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.core.database.entity.VideoDataEntity

@Dao
interface VideoDataDao {
    @Query("SELECT * FROM video_data WHERE user_id = :userId")
    fun getVideo(userId: String): VideoDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideo(entity: VideoDataEntity)

    @Query("DELETE FROM video_data WHERE user_id = :userId")
    fun deleteVideo(userId: String)
}